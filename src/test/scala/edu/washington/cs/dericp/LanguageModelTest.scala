package edu.washington.cs.dericp

import org.junit.Assert._
import org.junit.Test

class LanguageModelTest {

  val index : Map[String,List[DocData]] = Map("the" -> List(new DocData("0", 2), new DocData("1", 3), new DocData("2", 1)),
    "big" -> List(new DocData("0", 1), new DocData("2", 1)), "red" -> List(new DocData("1", 1)), "house" -> List(new DocData("1", 1), new DocData("2", 1)),
    "is" -> List(new DocData("0", 1), new DocData("1", 1), new DocData("2", 1)))
  val docLength : Map[String,Int] = Map("0" -> 4, "1" -> 6, "2" -> 4)

  val lambda = 0.01
  val lm: LanguageModel = new LanguageModel(index, docLength, lambda)
  val query = List("house", "is", "red")


  @Test
  def testFindPW(): Unit = {
    val pw = lm.findPW("red")
    assertEquals(1.toDouble/14, pw, 0)
  }

  @Test
  def testFindPWD(): Unit = {
    val pwd = lm.findPWD("is", "1")
    assertEquals(1.toDouble/6, pwd, 0)
  }

  @Test
  def testFindLogPQDSmoothSmall(): Unit = {
    val doc0 = lm.findLogPQDSmooth(query, "0", lambda)
    val doc1 = lm.findLogPQDSmooth(query, "1", lambda)
    val doc2 = lm.findLogPQDSmooth(query, "2", lambda)
    assertTrue(doc1 > doc2 && doc2 > doc0)
  }
}