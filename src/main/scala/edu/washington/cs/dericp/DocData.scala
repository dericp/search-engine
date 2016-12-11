package edu.washington.cs.dericp

class DocData(val docID: String, val freq: Int) extends Ordered[DocData] {
  def id() : String = {
    docID
  }

  def matches(that : DocData) : Int = {
    this.docID compare that.docID
  }

  def matched(that: DocData) : DocData = {
    new DocData(id(), this.freq + that.freq)
  }

  override def compare(that: DocData): Int = {
    this.matches(that)
  }

  override def toString: String = {
    this.docID + ":" + this.freq
  }

  override def equals(o: Any): Boolean = o match {
    case docData: DocData => this.docID.equals(docData.docID) && this.freq.equals(docData.freq)
    case _ => false
  }
}
