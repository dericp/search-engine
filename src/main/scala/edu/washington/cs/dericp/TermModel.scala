package edu.washington.cs.dericp

/**
  * Created by erikawolfe on 11/29/16.
  *
  *
  * UPDATE TERM MODEL TEST FILE
  *
  */
class TermModel(val index: Map[String,Seq[DocData]], val docLength: Map[String,Int]) extends RelevanceModel {
  // TODO: Normalize term frequency by max tf in document

  // Document frequencies = index.mapValues(_ => _.length)
  val numDocs = docLength.keySet.size

  // Map: term => idf(term)
  val idf = index.mapValues{ case(list) => Math.log(numDocs) - Math.log(list.size) }

  // Using simple scoring - sum of tf-idf scores of each query word, log of tf
  def tfIdfScore(query: Seq[String], docID: String): Double = {
    val ltf = query.filter(term => index.contains(term)).map( term => term -> Math.log(1 + index(term).find(_.docID == docID).getOrElse(new DocData("0", 0)).freq)).toMap
    ltf.map{ case(term, ltf) => ltf * idf.getOrElse(term, 0.0) }.sum
  }

  // Returns the top n docs for the given query using tf idf scoring
  def topNDocs(query: Seq[String], n: Int): Seq[String] = {
    val trimmedIndex = InvertedIndex.listIntersection(query, n, index)
    var shortenedDocList = docLength.keys
    println(trimmedIndex.size)
    if (!trimmedIndex.isEmpty) {
      shortenedDocList = shortenedDocList.filter(d => trimmedIndex.contains(d))
    }
    val tfIdfScores = shortenedDocList.map(docID => (docID, tfIdfScore(query, docID))).toVector
    tfIdfScores.sortBy(-_._2).take(n).map(_._1)
  }
}
