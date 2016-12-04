package edu.washington.cs.dericp
import ch.ethz.dal.tinyir.indexing.Result
import ch.ethz.dal.tinyir.processing.XMLDocument

class DocData(data: (Int, Int)) extends Ordered[DocData] {
  def id() : Int = {
    data._1
  }

  def matches(that : (Int, Int)) : Int = {
    data._1 - that._1
  }

  // need to do some term frequency stuff here
  def matched(that: (Int, Int)) : (Int, Int) = {
    (data._1, data._2 + that._2)
  }

  override def compare(that: DocData): Int = id() - that.id()

  override def toString: String = {
    data._1 + " " + data._2
  }
}
