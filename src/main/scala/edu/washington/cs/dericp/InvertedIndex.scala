package edu.washington.cs.dericp

import java.io.PrintWriter
import ch.ethz.dal.tinyir.io.TipsterStream
import com.github.aztek.porterstemmer.PorterStemmer
import scala.io.Source

/**
  * This object holds all the methods needed to create, write, and read an inverted index. It also
  * allows for a list intersection to be performed on an inverted index.
  */
object InvertedIndex {
  // the minimum number of documents a term must appear in --- this helps prune typos
  val MIN_NUM_OCCURRENCES = 2

  /**
    * Create an inverted index that is a map from term to a sequence of DocData objects
    * that represents the documents that contain the term.
    *
    * @param filepath the folder that contains the zipped documents collection that can
    *                 be read into a TipsterStream
    * @return the inverted index
    */
  def createInvertedIndex(filepath: String): Map[String, Seq[DocData]] = {
    // XMLDocument stream
    def docs = new TipsterStream(filepath).stream//.take(1000)
    docs.flatMap(doc => doc.tokens.filter(!Utils.STOP_WORDS.contains(_)).map(token => (PorterStemmer.stem(token), doc.name)))
    // [(token, docID), ...] minus stop words and stems
        .groupBy(_._1)
        // {token -> [(token, docID), ...], ...}
        .filter(_._2.size >= MIN_NUM_OCCURRENCES)
        // make sure the term isn't a typo
        .mapValues(_.map(tuple => tuple._2)
        // {token -> [docID, ...], ...}
        .groupBy(identity)
        // {token -> {docID -> [docID, docID, ...], ...}, ...}
        .map{ case(docID, docIDs) => (docID, docIDs.size) }
        // {token -> {docID -> docIDCount, ...}, ...}
        .map(tuple => new DocData(tuple._1, tuple._2)).toVector.sorted)
        // {token -> [DocData1, DocData2, ...], ...}
  }

  /**
    * Writes the inverted index to a file.
    *
    * @param invIdx the inverted index
    */
  def writeInvertedIndexToFile(invIdx: Map[String, Seq[DocData]], filepath: String): Unit = {
    val pw = new PrintWriter(filepath)

    // method writes a line to a file
    def writeLineToFile(termToDocDatas: (String, Seq[DocData])): Unit = {
      // write the term
      pw.print(termToDocDatas._1 + " ")
      // get the docIDs to counts
      val line = termToDocDatas._2.mkString(" ")
      pw.println(line)
    }

    invIdx.foreach(writeLineToFile(_))

    pw.close()
  }

  /**
    * Reads an inverted index from a file.
    *
    * @param filepath the filepath of the inverted-index which should have been generated using
    *                 writeInvertedIndex
    * @return the inverted index
    */
  def readInvertedIndexFromFile(filepath: String) : Map[String, Seq[DocData]] = {
    val invIdx = new collection.mutable.HashMap[String, Seq[DocData]]
    val lines: Iterator[Array[String]] = Source.fromFile(filepath).getLines().map(l => l.split("\\s+"))

    // add a line from the file to the inverted index
    def addLineToIndex(line: Array[String]): Unit = {
      if (!line.isEmpty) {
        val term = line(0)
        // collection of tuples (docID, freq)
        val docIDsAndFreqs = line.drop(1).map(str => str.split(":"))
        val docDatas = docIDsAndFreqs.map{ arr => new DocData(arr(0), arr(1).toInt) }.toVector
        invIdx += ((term, docDatas))
      }
    }

    lines.foreach(addLineToIndex(_))

    Map() ++ invIdx
  }


  /**
    * Returns a set of document IDs that contain at least a subset of the query terms.
    * If the number of documents returned cannot reach n, this method will return an empty set.
    *
    * @param query the query terms
    * @param invIdx the inverted index that the list intersection will be performed on
    * @return the set of document IDs that contain at least a subset of the query
    */
  def listIntersection(query: Seq[String], n: Int, invIdx: Map[String, Seq[DocData]]): Set[String] = {
    // list of the docIDs that contain all terms in the query
    val output = scala.collection.mutable.HashSet.empty[String]
    // the inverted index but only with the terms in the query
    val termToDocIDsOnlyQueryTerms: Map[String, Seq[String]] =
      invIdx.filter{ case(term, _) => query.contains(term) }.mapValues(docDatas => docDatas.map(_.docID).toVector)

    // case where none of the query terms show up in the documents
    if (termToDocIDsOnlyQueryTerms.isEmpty) {
      println("set is empty!")
      return Set.empty
    }

    // each terms mapped to the index we're currently looking at
    val termToCurrIdx = collection.mutable.Map() ++ termToDocIDsOnlyQueryTerms.mapValues(_ => 0)

    // see if we have reached the end of a term's doc list
    var keepSearching = true

    // increment the index for a term in termToCurrIdx, check if we have reached end of posting list
    def incrIdx(term: String): Unit = {
      val newIndex = termToCurrIdx(term) + 1
      if (newIndex < termToDocIDsOnlyQueryTerms(term).size) {
        termToCurrIdx(term) = newIndex
      } else {
        keepSearching = false
      }
    }

    // find min based on doc ID (_2) and return corresponding term
    def min(a: (String, String), b: (String, String)) : (String, String) = {
      if (a._2 < b._2) a
      else b
    }

    // find max based on doc ID (_2) and return corresponding term
    def max(a: (String, String), b: (String, String)) : (String, String) = {
      if (a._2 > b._2) a
      else b
    }

    // continuously search for intersections
    while (keepSearching) {
      // look at the current doc for each term
      val termToCurrDocID = termToCurrIdx.map{ case(term, index) => (term, termToDocIDsOnlyQueryTerms(term)(index)) }
      val termWithLowestIdx = termToCurrDocID.foldRight(("", "ZZZZZZZZZZZZZZZ"))(min)._1
      val docIDWithLowestIdx = termToCurrDocID(termWithLowestIdx)
      val docIDWithHighestIdx = termToCurrDocID(termToCurrDocID.foldRight(("", ""))(max)._1)

      // if lowest doc == highest doc, we found intersection
      // otherwise increment the lowest doc and keep searching
      if (docIDWithLowestIdx.equals(docIDWithHighestIdx)) {
        output += docIDWithHighestIdx
        termToCurrIdx.foreach{ case (term, _) => incrIdx(term) }
      } else {
        incrIdx(termWithLowestIdx)
      }
    }

    // if we didn't find enough terms, delete the term that occurs in the most docs from the query and go again
    if (output.size < n) {
      val maxTerm = termToDocIDsOnlyQueryTerms.keysIterator
        .reduceLeft((x, y) => if (termToDocIDsOnlyQueryTerms(x).size > termToDocIDsOnlyQueryTerms(y).size) x else y)
      listIntersection(query.filter(!_.equals(maxTerm)), n, invIdx)
    } else {
      // returning the final list of doc IDs with all query terms
      println(query.size)
      output.toSet
    }
  }
}
