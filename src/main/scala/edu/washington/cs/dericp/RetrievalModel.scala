package edu.washington.cs.dericp

/**
  * Created by Isa on 12/11/2016.
  */
trait RetrievalModel {
  def topNDocs(query: List[String], n: Int) : List[String]
}
