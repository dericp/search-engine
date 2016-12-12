package edu.washington.cs.dericp

/**
  * A relevance model searches for documents based on a query.
  */
trait RelevanceModel {

  /**
    * Get the top n documents given a search query.
    *
    * @param query
    * @param n
    * @return
    */
  def topNDocs(query: Seq[String], n: Int): Seq[String]

}
