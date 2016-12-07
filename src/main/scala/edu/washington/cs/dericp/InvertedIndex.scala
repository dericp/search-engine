package edu.washington.cs.dericp
import java.io.PrintWriter

import ch.ethz.dal.tinyir.processing.XMLDocument

import scala.collection.immutable.HashMap
import scala.io.Source

/**
  * Created by dericp on 11/29/16.
  *
  * FIX INVERTED INDEX TEST
  */
object InvertedIndex {
  // String, Int because docID is always -1, using doc name

  // TODO: Better to convert to getting frequencies of each document and re-ordering into inverted index?
  def invertedIndex(docs : Stream[XMLDocument]) : Map[String, List[(String, Int)]] = {
   postings(docs).toList.groupBy(_._1).mapValues(_.map(p => p._2).groupBy(identity).map{ case(id, list) => (id, list.size) }.toList)
  }

  def postings (s: Stream[XMLDocument]): Stream[(String,String)] =
    s.flatMap( d => d.tokens.map(token => (token,d.name) ))

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
      if (line.isEmpty) { return }
      val word = line(0)
      // pairs "doc,freq" without parens
      def docFreqs = line.slice(1, line.length).map(str => str.substring(1, str.length - 1))
      val docFreqPairs = docFreqs.map{str =>
        val a = str.split(",")
        (a(0), a(1).toInt)
      }.toList
      index.+=((word, docFreqPairs))
    }

    lines.foreach(addLineToIndex(_))


    Map().++(index)
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

