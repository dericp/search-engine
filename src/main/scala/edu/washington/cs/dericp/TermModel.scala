package edu.washington.cs.dericp

import breeze.util.TopK

/**
  * Created by erikawolfe on 11/29/16.
  *
  *
  * UPDATE TERM MODEL TEST FILE
  *
  */
class TermModel(val index: Map[String,List[(String,Int)]], val docLength: Map[String,Int]) {
  // TODO: Normalize term frequency by max tf in document

  // Document frequencies = index.mapValues(_ => _.length)
  val numDocs = docLength.keySet.size

  // Map: term => idf(term)
  val idf = index.mapValues{ case(list) => Math.log(numDocs) - Math.log(list.size) }

  // Using simple scoring - sum of tf-idf scores of each query word, log of tf
  // TODO: Convert to use vector-based model and more complex scoring?
  def tfIdfScore(query: List[String], docID: String): Double = {
    val ltf = query.map( term => term -> Math.log(1 + index(term).find(_._1 == docID).getOrElse((0, 0))._2)).toMap
    ltf.map{ case(term, ltf) => ltf * idf.getOrElse(term, 0.0) }.sum
  }

  // TODO: Returns the top
  def topNDocs(query: List[String], n: Int, shortenedDocList: Seq[String]): List[String] = {
    val tfIdfScores = shortenedDocList.map(docID => (docID, tfIdfScore(query, docID))).toList
    tfIdfScores.sortBy(-_._2).take(n).map(_._1)
  }
}
