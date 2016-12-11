package edu.washington.cs.dericp

import breeze.util.TopK

import scala.math.log

/**
  * Created by Isa on 11/27/2016.
  */
class LanguageModel(val index: Map[String, Seq[DocData]], val docLengths: Map[String,Int], lambda: Double)
  extends RelevanceModel {
  // currently a unigram model

  def getDocDataFromList(doc: String, list: Seq[DocData]) : DocData = {
    val filtered = list.filter(dd => dd.id() == doc)
    // technically should always work bc id should only exist once, but be careful
    filtered.head
  }

  // find P(w)
  def findPW(word: String) : Double = {
    val wordFreq = index.getOrElse(word, List.empty).map(_.freq).sum
    val corpusSize = docLengths.values.sum
    wordFreq.toDouble / corpusSize
  }

  // find log(P(w|d))
  def findPWD(word: String, doc: String) : Double = {
    val docsWithFreqs = index.getOrElse(word, List.empty)
    // need to add smoothing to fix 0 freq, and fix doc length = 0 (applies for non smoothed below)
    val tf = getDocDataFromList(doc, docsWithFreqs).freq
    tf.toDouble / docLengths.getOrElse(doc, 0)
  }

  // TODO: probably delete this later!!!!!
  // find P(q|d)
  def findLogPQD(query: Seq[String], doc: String) : Double = {
    query.map(w => log(findPWD(w, doc))).sum
  }

  // find P(q|d) with Jelinek-Mercer smoothing
  def findLogPQDSmooth(query: Seq[String], doc: String, lambda: Double) : Double = {
    val dqIntersection = query.filter(w => !index.getOrElse(w, List.empty).filter(a => a.id() == doc).isEmpty)
    val termProbs = dqIntersection.map(w => log(1 + ((1 - lambda) / lambda) * (findPWD(w, doc) / findPW(w)))).sum
    termProbs + log(lambda)
  }

  def topNDocs(query: Seq[String], n: Int): Seq[String] = {
    val trimmedIndex = InvertedIndex.listIntersection(query, n, index)
    var docsToSearch = docLengths.keys
    //println(containAllQueryWords.size)
    if (!trimmedIndex.isEmpty) {
      docsToSearch = docsToSearch.filter(d => trimmedIndex.contains(d))
    }
    val pdqs = docsToSearch.map(d => (d, findLogPQDSmooth(query, d, lambda))).toIndexedSeq
    pdqs.sortBy(-_._2).take(n).map(_._1)
  }


}
