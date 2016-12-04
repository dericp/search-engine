package edu.washington.cs.dericp

import breeze.util.TopK

/**
  * Created by erikawolfe on 11/29/16.
  */
class TermModel(val index: Map[String,List[(Int,Int)]], val docLength: Map[Int,Int]) {
  // TODO: Normalize term frequency by max tf in document

  // Document frequencies = index.mapValues(_ => _.length)
  val numDocs = docLength.keySet.size

  // Map: term => idf(term)
  val idf = index.mapValues{ case(list) => Math.log(numDocs) - Math.log(list.size) }

  // Using simple scoring - sum of tf-idf scores of each query word, log of tf
  // TODO: Convert to use vector-based model and more complex scoring?
  def tfIdfScore(query: List[String], docID: Int): Double = {
    val ltf = query.map( term => term -> Math.log(1 + index(term).find(_._1 == docID).getOrElse((0, 0))._2)).toMap
    ltf.map{ case(term, ltf) => ltf * idf.getOrElse(term, 0.0) }.sum
  }

  // TODO: Once document representation is decided, use TopK to return top 100 docs
  def top100Docs(query: List[String]): TopK[Int] = {
    val result = new TopK[Int](100)

  }
}
