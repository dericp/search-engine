package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.{Document, XMLDocument}

/**
  * Created by Isa on 11/27/2016.
  */

case class TfTuple(term: String, doc: Int, count: Int)

object Main {

  // currently just code from slides, not tested or checked, feel free to change
  def testinvertedindex(): Unit = {

    // TODO: Use the whole document stream
    // NOTE: doc.id = -1 because in Document id is assigned to name.toInt else -1 and name is not an int!
    val docs = new TipsterStream ("src/main/resources/documents").stream.take(1000)

    def postings (s: Stream[XMLDocument]): Stream[(String,String)] =
      s.flatMap( d => d.tokens.map(token => (token,d.name) ))

    // word => List(doc.name, tf of word in doc)
    val b = postings(docs).toList.groupBy(_._1).mapValues(_.map(p => p._2).groupBy(identity).map{ case(id, list) => (id, list.size) }.toList) //.distinct.sorted)
    println(b.toList)

    //---------------------
    // i wrote language model based on having an inverted index w/ term frequency
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
    // testinvertedindex()
    val docs = new TipsterStream ("src/main/resources/documents").stream.take(1000)
    val invInd = new InvertedIndex(docs).invertedIndex
  }
}

