package edu.washington.cs.dericp
import ch.ethz.dal.tinyir.indexing.Result
import ch.ethz.dal.tinyir.processing.XMLDocument

class DocData(val docID: String, val freq: Int) extends Ordered[DocData] {
  def id() : String = {
    docID
  }

  def matches(that : DocData) : Int = {
    this.docID compare that.docID
  }

  // need to do some term frequency stuff here
  def matched(that: DocData) : DocData = {
    new DocData(id(), this.freq + that.freq)
  }

  override def compare(that: DocData): Int = {
    this.matches(that)
  }

  override def toString: String = {
    this.docID + " " + this.freq
  }
}
