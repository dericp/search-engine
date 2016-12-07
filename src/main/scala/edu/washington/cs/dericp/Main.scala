package edu.washington.cs.dericp
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.{Document, XMLDocument}

/**
  * Created by Isa on 11/27/2016.
  */

case class TfTuple(term: String, doc: Int, count: Int)

object Main {
//    def tfTuples (docs: Stream[Document]) : Stream[TfTuple] =
//      docs.flatMap( d => d.tokens.groupBy(identity)
//        .map{ case (tk,lst) => TfTuple(tk, d.ID, lst.length) }
//
//    val fqIndex : Map[String,List[(Int,Int)]] =
//      tfTuples(docs).groupBy(_.term)
//        .mapValues(_.map(tfT => (tfT.doc, tfT.count)).sorted

  def main(args: Array[String]): Unit = {
    // TODO: Use the whole document stream

    val docs = new TipsterStream ("src/main/resources/documents").stream.take(10)
    val invInd1 = InvertedIndex.invertedIndex(docs)
    println("finished building index")
    // InvertedIndex.printIndexToFile(invInd1)

    // val invInd2 = InvertedIndex.readIndexFromFile("src/main/resources/inverted-index.txt")
    // println(invInd1)
    // println(invInd2)
    // println("hiii")


    ///////// INVERTED INDEX USING TERM MODEL TESTING
    //    val docLengths = docs.map(doc => (doc.name -> doc.tokens.length)).toMap
//    val termModel = new TermModel(invInd, docLengths)
//
//    // common words: society, cages, 000
//    val doc1 = docs(0)
//    // common words: his, george, hampshire, dole, buckley
//    val doc2 = docs(1)
//    println("doc1 name: " + doc1.name)
//
//    // val query = List("his", "hampshire", "dole", "buckley")
//    val query = List("society", "cages", "000")
//
//    // println("score 1 (should be low): " + termModel.tfIdfScore(query, doc1.name))
//    // println("score 2 (should be higher): " + termModel.tfIdfScore(query, doc2.name))
//
//    println(termModel.topNDocs(query, 15))

  }
}

