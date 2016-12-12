package edu.washington.cs.dericp

/**
  * This class represents the data of a document.
  *
  * @param docID the document ID
  * @param freq the frequency of a term inside the document
  */
class DocData(val docID: String, val freq: Int) extends Ordered[DocData] {

  override def compare(that: DocData): Int = {
    this.docID compare that.docID
  }

  override def toString: String = {
    this.docID + ":" + this.freq
  }

  override def equals(o: Any): Boolean = o match {
    case docData: DocData => this.docID.equals(docData.docID) && this.freq.equals(docData.freq)
    case _ => false
  }
}
