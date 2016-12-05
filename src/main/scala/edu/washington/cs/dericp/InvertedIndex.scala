package edu.washington.cs.dericp
import ch.ethz.dal.tinyir.processing.XMLDocument

/**
  * Created by dericp on 11/29/16.
  */
class InvertedIndex(val docs : Stream[XMLDocument]) {

//  def invertedIndex : Map[String, List[DocData]] = {
//    docs.flatMap(doc => doc.tokens.map( token => ( token, new DocData((doc.ID, 1)) ) ).groupBy(_._1).mapValues(_.map(t => t._2).distinct.sorted.toList)).toMap
//  }

  // String, Int because docID is always -1, using doc name
  def invertedIndex : Map[String, List[(String, Int)]] = {
   postings(docs).toList.groupBy(_._1).mapValues(_.map(p => p._2).groupBy(identity).map{ case(id, list) => (id, list.size) }.toList)
  }

  def postings (s: Stream[XMLDocument]): Stream[(String,String)] =
    s.flatMap( d => d.tokens.map(token => (token,d.name) ))

  // if l1.length << l2.length, -> O(l1.length * log(l2.length))
  // TODO: (?) ^ add test for if length is much smaller, implement binary search
  /*def intersect [A <% Result[A]] (l1: List[A], l2: List[A]) : List[A] = {
    var result = List[A]()
    val len1 = l1.length
    val len2 = l2.length
    var index1 = 0
    var index2 = 0

    while(index1 < len1 && index2 < len2) {
      val n = l1(index1) matches l2(index2)
      if (n > 0) {
        index2 += 1
      } else if (n < 0) {
        index1 += 1
      } else {
        result = result.::(l1(index1))
        index1 += 1
        index2 += 1
      }
    }
    result
  }*/
}

