package edu.washington.cs.dericp

import scala.math.log

class LanguageModel(val index: Map[String, Seq[DocData]], val originalDocLengths: Map[String,Int])
  extends RelevanceModel {
  val docLengths = originalDocLengths.mapValues(_ + 200)
  val LAMBDA = 0.1
  /**
    * Takes in a docID and a list of DocData objects and returns the DocData containing the matching the docID
    *
    * @param doc
    * @param list
    * @return matching DocData
    */
  def getDocDataFromList(doc: String, list: Seq[DocData]) : DocData = {
    val filtered = list.filter(dd => dd.docID == doc)
    // technically should always work bc id should only exist once, but be careful
    filtered.head
  }

  /**
    * Finds the probability of the word in the entire index
    *
    * @param word
    * @return P(w)
    */
  def findPW(word: String) : Double = {
    val wordFreq = index.getOrElse(word, List.empty).map(_.freq).sum
    val corpusSize = docLengths.values.sum
    wordFreq.toDouble / corpusSize
  }

  /**
    * Finds the probability of a certain word given a specific document
    *
    * @param word
    * @param doc
    * @return  P(w|d)
    */
  def findPWD(word: String, doc: String) : Double = {
    val docsWithFreqs = index.getOrElse(word, List.empty)
    // need to add smoothing to fix 0 freq, and fix doc length = 0 (applies for non smoothed below)
    val tf = getDocDataFromList(doc, docsWithFreqs).freq
    tf.toDouble / docLengths.getOrElse(doc, 0)
  }

  /**
    * Find the probability of a certain query given a specific document using Jelinek-Mercer smoothing
    *
    * @param query
    * @param doc
    * @return P(q|d)
    */
  def findLogPQDSmooth(query: Seq[String], doc: String) : Double = {
    val dqIntersection = query.filter(w => !index.getOrElse(w, List.empty).filter(a => a.docID == doc).isEmpty)
    val termProbs = dqIntersection.map(w => log(1 + ((1 - LAMBDA) / LAMBDA) * (findPWD(w, doc) / findPW(w)))).sum
    termProbs + log(LAMBDA)
  }

  /**
    * Returns an ordered list of the top n relevant docs for a given query
    *
    * @param query
    * @param n
    * @return search results
    */
  def topNDocs(query: Seq[String], n: Int): Seq[String] = {
    val trimmedIndex = InvertedIndex.listIntersection(query, n, index)
    var docsToSearch = docLengths.keys
    if (!trimmedIndex.isEmpty) {
      docsToSearch = docsToSearch.filter(d => trimmedIndex.contains(d))
    }
    val pdqs = docsToSearch.map(d => (d, findLogPQDSmooth(query, d))).toIndexedSeq
    pdqs.sortBy(-_._2).take(n).map(_._1)
  }


}
