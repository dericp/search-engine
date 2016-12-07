package edu.washington.cs.dericp

import breeze.util.TopK

import scala.math.log

/**
  * Created by Isa on 11/27/2016.
  */
class LanguageModel(val index: Map[String,List[(String,Int)]], val docLength: Map[String,Int]) {
  // currently a unigram model

  // find P(w)
  def findPW(word: String) : Double = {
    val wordFreq = index.getOrElse(word, List.empty).map(_._2).sum
    val corpusSize = docLength.values.sum
    wordFreq.toDouble / corpusSize
  }

  // find log(P(w|d))
  def findPWD(word: String, doc: String) : Double = {
    val docsWithFreqs = index.getOrElse(word, List.empty).toMap
    // need to add smoothing to fix 0 freq, and fix doc length = 0 (applies for non smoothed below)
    val tf = docsWithFreqs.getOrElse(doc, 0)
    tf.toDouble / docLength.getOrElse(doc, 0)
  }

  // probably delete this later!!!!!
  // find P(q|d)
  def findLogPQD(query: List[String], doc: String) : Double = {
    query.map(w => log(findPWD(w, doc))).sum
  }

  // find P(q|d) with Jelinek-Mercer smoothing
  def findLogPQDSmooth(query: List[String], doc: String, lambda: Double) : Double = {
    val dqIntersection = query.filter(w => !index.getOrElse(w, List.empty).filter(a => a._1 == doc).isEmpty)
    val termProbs = dqIntersection.map(w => log(1 + ((1 - lambda) / lambda) * (findPWD(w, doc) / findPW(w)))).sum
    termProbs + log(lambda)
  }

  // TODO:  Write method to return top 100 docs by logpqd
  def top100Docs(query: String): TopK[Int] = {
    // consider using TopK from breeze to keep top 100
    val result = new TopK[Int](100)
    result
  }
}
