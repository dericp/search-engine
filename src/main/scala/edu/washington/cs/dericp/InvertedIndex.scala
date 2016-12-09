package edu.washington.cs.dericp
import java.io.PrintWriter

import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.io.Source

object InvertedIndex {
  // the minimum number of documents a word must appear in --- this helps prune typos
  val MIN_NUM_DOCS = 5

  /**
    * Builds an inverted index and returns it as a Map.
    *
    * @param docs stream of XMLDocument objects that represents the document collection
    * @return map from the term/word to a list of DocData objects
    */
  def invertedIndex(docs : Stream[XMLDocument]) : Map[String, List[DocData]] = {
    // TODO: Figure out if we should be more careful when deleting word pairs with low document frequency
    postings(docs)
    // [(token, docID), ...]
        .groupBy(_._1)
        // {token -> [(token, docID), ...], ...}
        .mapValues(_.map(tuple => tuple._2)
        // {token -> [docID, ...], ...}
        .groupBy(identity)
        // {token -> {docID -> [docID, docID, ...], ...}, ...}
        .map{ case(docID, docIDs) => (docID, docIDs.size) }
        // {token -> {docID -> docIDCount, ...}, ...}
        .map(tuple => new DocData(tuple._1, tuple._2)).toList.sorted)
        // {token -> [DocData1, DocData2, ...], ...}
        // get rip of stop words and rarely occuring words
        .filter{ case(key, value) => !Utils.STOP_WORDS.contains(key) && value.length > MIN_NUM_DOCS }
  }

  /**
    * Creates the postings list.
    *
    * @param docs stream of XMLDocument objects that represents the document collection
    * @return stream of tuples (token, docID)
    */
  def postings (docs: Stream[XMLDocument]): Stream[(String, String)] = {
    docs.flatMap(doc => doc.tokens.map(token => (token, doc.name)))
  }

  /**
    * Writes the inverted index to a file.
    *
    * @param invIdx the inverted index
    */
  def writeInvertedIndexToFile(invIdx: Map[String, List[(String, Int)]]): Unit = {
    val pw = new PrintWriter("src/main/resources/inverted-index.txt")

    // method writes a line to a file
    def writeLineToFile(wordVals: (String, List[(String, Int)])): Unit = {
      pw.print(wordVals._1 + " ")  // writing word
      val line = wordVals._2.mkString(" ")
      pw.println(line)
    }

    invIdx.foreach(writeLineToFile(_))

    pw.close()
  }

  /**
    * Reads an inverted index from a file.
    *
    * @param filepath
    * @return
    */
  def readInvertedIndexFromFile(filepath: String): Map[String, List[(String, Int)]] = {
    val invIdx = new collection.mutable.HashMap[String, List[(String, Int)]]
    val lines: Iterator[Array[String]] = Source.fromFile(filepath).getLines().map(l => l.split("\\s+"))

    // add a line from the file to the inverted index
    def addLineToIndex(line: Array[String]): Unit = {
      if (!line.isEmpty) {
        val word = line(0)
        // pairs "doc,freq" without parens
        // TODO: why is docFreqs a def?
        def docFreqs = line.slice(1, line.length).map(str => str.substring(1, str.length - 1))
        val docFreqPairs = docFreqs.map { str =>
          val a = str.split(",")
          (a(0), a(1).toInt)
        }.toList
        invIdx.+=((word, docFreqPairs))
      }
    }

    lines.foreach(addLineToIndex(_))

    Map() ++ invIdx
  }


  // TODO: need to test this, no idea if it actually works
  def listIntersection(query: List[String], index: Map[String, List[DocData]]) : List[String] = {
    // create output map, trimmed inverted index, and index counter
    val output = scala.collection.mutable.ListBuffer.empty[String]
    // why dis is vector
    val queryIndex: Map[String, Vector[String]] = index.filter{case(term, _) => query.contains(term)}.mapValues(l => l.map(_.id()).to[Vector])//_.to[Vector])
    // doc id list for each term to index we're looking at
    val counter = collection.mutable.Map() ++ queryIndex.mapValues(_ => 0)

    // see if we have reached the end of a term's posting list
    var keepSearching = true

    // increment the index for a term in counter, check if we have reached end of posting list
    def incrIndex(term: String): Unit = {
      val newIndex = counter(term) + 1
      if (newIndex < queryIndex(term).size) {
        counter(term) = newIndex
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
      val termToCurrentDoc = counter.map{ case(term, index) => (term, queryIndex(term)(index)) }
      val lowestTermTuple = termToCurrentDoc.foldRight(("", "ZZZZZZZZZZZZZZZ"))(min)
      val highestTermDoc = termToCurrentDoc(termToCurrentDoc.foldRight(("", ""))(max)._1)
      println("lowest: " + lowestTermTuple)
      println("hightest: " + highestTermDoc)
      println("==========================")
      // if lowest doc == highest doc, we found intersection, otherwise increment the lowest doc and keep searching
      if (termToCurrentDoc(lowestTermTuple._1).equals(highestTermDoc)) {
        println("hiiiii")
        //println(highestTermDoc)
        output += highestTermDoc
        counter.foreach{case (term, _) => incrIndex(term)}
      } else {
        println("hello")
        incrIndex(lowestTermTuple._1)
      }
    }

    // returning the final list of doc IDs with all query words
    output.toList
  }
}

