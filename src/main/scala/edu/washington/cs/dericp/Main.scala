package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream

object Main {

  def main(args: Array[String]): Unit = {
    val docs = new TipsterStream ("src/main/resources/documents").stream.take(10)
    val invIdx = InvertedIndex.invertedIndex(docs)
    println("finished building index")

    val shortDocList = InvertedIndex.listIntersection(List("the"), invIdx)



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

