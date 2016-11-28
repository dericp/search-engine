package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Document

/**
  * Created by Isa on 11/27/2016.
  */

case class TfTuple(term: String, doc: Int, count: Int)

object Main {

  // currently just code from slides, not tested or checked, feel free to change
  def testinvertedindex(): Unit = {

    def docs = new TipsterStream ("src/main/resources/documents").stream

    def postings (s: Stream[Document]): Stream[(String,Int)] =
      s.flatMap( d => d.tokens.map(token => (token,d.ID)) )

    val a = postings(docs).groupBy(_._1).mapValues(_.map(p => p._2).distinct.sorted)

    //---------------------
//
//    def tfTuples (docs: Stream[Document]) : Stream[TfTuple] =
//      docs.flatMap( d => d.tokens.groupBy(identity)
//        .map{ case (tk,lst) => TfTuple(tk, d.ID, lst.length) }
//
//    val fqIndex : Map[String,List[(Int,Int)]] =
//      tfTuples(docs).groupBy(_.term)
//        .mapValues(_.map(tfT => (tfT.doc, tfT.count)).sorted)
  }

  def main(args: Array[String]): Unit = {
    def docs = new TipsterStream ("src/main/resources/documents")

    val a = docs


    // TESTING LANGUAGE MODEL
    val lm = new LanguageModel
    val query = List("house", "is", "red")
    val lambda = .01
    println("0 -> " + lm.findLogPQDSmooth(query, 0, lambda).toString)
    println("1 -> " + lm.findLogPQDSmooth(query, 1, lambda).toString)
    println("2 -> " + lm.findLogPQDSmooth(query, 2, lambda).toString)
    // DONE TESTING
  }

}
