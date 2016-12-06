package edu.washington.cs.dericp

import org.junit.Assert._
import org.junit.Test

/**
  * Created by erikawolfe on 11/29/16.
  */
class TermModelTest {
//  val index : Map[String,List[(Int,Int)]] = Map("the" -> List((0, 2), (1, 3), (2, 1)),
//    "big" -> List((0, 1), (2, 1)), "red" -> List((1, 1)), "house" -> List((1, 1), (2, 1)),
//    "is" -> List((0, 1), (1, 1), (2, 1)))
//  val numDocs = 3.0
//  val docLength : Map[Int,Int] = Map(0 -> 4, 1 -> 6, 2 -> 4)
//  val tm: TermModel = new TermModel(index, docLength)
//
//  @Test
//  def testIdf(): Unit = {
//    val idf = tm.idf
//    val res = Map("the" -> Math.log(numDocs / 3), "big" -> Math.log(numDocs / 2), "red" -> Math.log(numDocs / 1),
//      "house" -> Math.log(numDocs / 2), "is" -> Math.log(numDocs / 3))
//    for (key <- res.keySet) {
//      assertEquals("For key: " + key, res.getOrElse(key, 0.0), idf.getOrElse(key, 0.0), 0.00001)
//    }
//  }
//
//  @Test
//  def testTfIdfScore: Unit = {
//    val query = List("big", "house")
//    val medScore = tm.tfIdfScore(query, 1)
//    val highScore = tm.tfIdfScore(query, 2)
//    assert(medScore < highScore)
//  }

}
