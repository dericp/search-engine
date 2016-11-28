package edu.washington.cs.dericp

import scala.math.log

/**
  * Created by Isa on 11/27/2016.
  */
class LanguageModel {
  // currently a unigram model

  // test variables, should reflect entire corpus in final version
  val index : Map[String,List[(Int,Int)]] = Map("the" -> List((0, 2), (1, 3), (2, 1)),
    "big" -> List((0, 1), (2, 1)), "red" -> List((1, 1)), "house" -> List((1, 1), (2, 1)),
    "is" -> List((0, 1), (1, 1), (2, 1))) //null
  val docLength : Map[Int,Int] = Map(0 -> 4, 1 -> 6, 2 -> 4) //null


  // find P(w)
  def findPW(word: String) : Double = {
    val wordFreq = index.getOrElse(word, List.empty).map(_._2).sum
    val corpusSize = docLength.values.sum
    wordFreq.toDouble / corpusSize
  }

  // find log(P(w|d))
  def findPWD(word: String, doc: Int) : Double = {
    val docsWithFreqs = index.getOrElse(word, List.empty).toMap
    // need to add smoothing to fix 0 freq, and fix doc length = 0 (applies for non smoothed below)
    val tf = docsWithFreqs.getOrElse(doc, 0)
    tf.toDouble / docLength.getOrElse(doc, 0)
  }

  // find P(q|d)
  def findLogPQD(query: List[String], doc: Int) : Double = {
    query.map(w => log(findPWD(w, doc))).sum
  }

  // find P(q|d) with Jelinek-Mercer smoothing
  def findLogPQDSmooth(query: List[String], doc: Int, lambda: Double) : Double = {
    val dqIntersection = query.filter(w => !index.getOrElse(w, List.empty).filter(a => a._1 == doc).isEmpty)
    val termProbs = dqIntersection.map(w => log(1 + ((1 - lambda) / lambda) * (findPWD(w, doc) / findPW(w)))).sum
    termProbs + log(lambda)
  }
}
