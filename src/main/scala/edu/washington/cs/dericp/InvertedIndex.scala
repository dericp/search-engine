package edu.washington.cs.dericp
import java.io.PrintWriter

import ch.ethz.dal.tinyir.processing.XMLDocument

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by dericp on 11/29/16.
  *
  * FIX INVERTED INDEX TEST
  */
object InvertedIndex {
  val MIN_NUM_DOCS = 5

  def invertedIndex(docs : Stream[XMLDocument]) : Map[String, List[DocData]] = {
    postings(docs).groupBy(_._1).mapValues(_.map(p => p._2).groupBy(identity).map{ case(id, list) => (id, list.size) }.
      map(tuple => new DocData(tuple._1, tuple._2)).toList.sorted).
      filter{ case(key, value) => !Utils.STOP_WORDS.contains(key) && value.length > MIN_NUM_DOCS }
    // TODO: Figure out if we should be more careful when deleting word pairs with low document frequency
  }

  // returns a collection of tuples, (token, docData)
  def postings (s: Stream[XMLDocument]): Stream[(String,String)] =
    s.flatMap( d => d.tokens.map(token => (token, d.name) ))

  def printIndexToFile(invInd: Map[String, List[(String, Int)]]): Unit = {
    val pw = new PrintWriter("src/main/resources/inverted-index.txt")
    invInd.foreach(writeLineToFile(_))

    def writeLineToFile(wordVals: (String, List[(String, Int)])): Unit = {
      pw.print(wordVals._1 + " ")  // writing word
      val line = wordVals._2.mkString(" ")
      pw.println(line)
    }

    pw.close()
  }

  def readIndexFromFile(filePath: String): Map[String, List[(String, Int)]] = {
    val index = new collection.mutable.HashMap[String, List[(String, Int)]]
    val lines: Iterator[Array[String]] = Source.fromFile(filePath).getLines().map(l => l.split("\\s+"))

    def addLineToIndex(line: Array[String]): Unit = {
      if (!line.isEmpty) {
        val word = line(0)
        // pairs "doc,freq" without parens
        def docFreqs = line.slice(1, line.length).map(str => str.substring(1, str.length - 1))
        val docFreqPairs = docFreqs.map { str =>
          val a = str.split(",")
          (a(0), a(1).toInt)
        }.toList
        index.+=((word, docFreqPairs))
      }
    }

    lines.foreach(addLineToIndex(_))


    Map().++(index)
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





  // if l1.length << l2.length, -> O(l1.length * log(l2.length))
  // TODO: (?) ^ add test for if length is much smaller, implement binary search
  /*def intersect [A <% Result[A]] (l1: List[A], l2: List[A]) : List[A] = {
    var result = List[A]()
    val len1 = l1.length
    val len2 = l2.length
    var index1 = 0
    var index2 = 0

    while(index1 < len1 && index2 < len2) {
      val n = l1(index1) matches l2(index2)
      if (n > 0) {
        index2 += 1
      } else if (n < 0) {
        index1 += 1
      } else {
        result = result.::(l1(index1))
        index1 += 1
        index2 += 1
      }
    }
    result
  }*/
}

