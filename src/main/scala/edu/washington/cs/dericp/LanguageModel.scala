package edu.washington.cs.dericp

import breeze.util.TopK

import scala.math.log

/**
  * Created by Isa on 11/27/2016.
  */
class LanguageModel(val index: Map[String,List[DocData]], val docLength: Map[String,Int], lambda: Double)
  extends RetrievalModel {
  // currently a unigram model

  def getDocDataFromList(doc: String, list: List[DocData]) : DocData = {
    val filtered = list.filter(dd => dd.id() == doc)
    // technically should always work bc id should only exist once, but be careful
    filtered.head
  }

  // find P(w)
  def findPW(word: String) : Double = {
    val wordFreq = index.getOrElse(word, List.empty).map(_.freq).sum
    val corpusSize = docLength.values.sum
    wordFreq.toDouble / corpusSize
  }

  // find log(P(w|d))
  def findPWD(word: String, doc: String) : Double = {
    val docsWithFreqs = index.getOrElse(word, List.empty)
    // need to add smoothing to fix 0 freq, and fix doc length = 0 (applies for non smoothed below)
    val tf = getDocDataFromList(doc, docsWithFreqs).freq
    tf.toDouble / docLength.getOrElse(doc, 0)
  }

  // TODO: probably delete this later!!!!!
  // find P(q|d)
  def findLogPQD(query: List[String], doc: String) : Double = {
    query.map(w => log(findPWD(w, doc))).sum
  }

  // find P(q|d) with Jelinek-Mercer smoothing
  def findLogPQDSmooth(query: List[String], doc: String, lambda: Double) : Double = {
    val dqIntersection = query.filter(w => !index.getOrElse(w, List.empty).filter(a => a.id() == doc).isEmpty)
    val termProbs = dqIntersection.map(w => log(1 + ((1 - lambda) / lambda) * (findPWD(w, doc) / findPW(w)))).sum
    termProbs + log(lambda)
  }

  // TODO: if less docs that contain all words, do we just search on everything or do we priotitize docs with everything
  def topNDocs(query: List[String], n: Int): List[String] = {
    val containAllQueryWords = InvertedIndex.listIntersection(query, index)
    var docsToSearch = docLength.keys
    println(containAllQueryWords.size)
    if (containAllQueryWords.size >= n) {
      docsToSearch = docsToSearch.filter(d => containAllQueryWords.contains(d))
    }
    val pdqs = docsToSearch.map(d => (d, findLogPQDSmooth(query, d, lambda))).toList
    pdqs.sortBy(-_._2).take(n).map(_._1)
  }


}
