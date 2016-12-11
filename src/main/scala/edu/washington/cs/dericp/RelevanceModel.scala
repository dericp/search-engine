package edu.washington.cs.dericp

trait RelevanceModel {
  def topNDocs(query: Seq[String], n: Int): Seq[String]
}
