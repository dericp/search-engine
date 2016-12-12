package edu.washington.cs.dericp

import edu.washington.cs.dericp.ScoringResources.Scores
import org.junit.Assert._
import org.junit.Test

/**
  * Created by Isa on 12/10/2016.
  */
class ScoringResourcesTest {

  def results = List("0", "1", "2", "3")
  def correctResults = Set("0", "1", "4", "5")

  def avgPrecResults = List("0", "1", "3", "4", "5")
  def avgPrecShortResults = List("0", "1", "3", "4")
  def avgPrecCorrectResults = Set("0", "3", "2", "4", "6")

  @Test
  def testPrecision(): Unit = {
    val p = ScoringResources.precision(4, 4)
    assertEquals(0.5, p, 0)
  }

  @Test
  def testRecall(): Unit = {
    val r = ScoringResources.recall(4, 2)
    assertEquals(4.0/6.0, r, 0)
  }

  @Test
  def testF1ScoreSimple(): Unit = {
    val f1 = ScoringResources.f1Score(0.5, 0.5)
    val expected = 2 * (0.5 * 0.5) / (0.5 + 0.5)
    assertEquals(expected, f1, 0)
  }

  @Test
  def testF1ScoreComplex(): Unit = {
    val f1 = ScoringResources.f1Score(4, 4, 4)
    val expected = 2 * (0.5 * 0.5) / (0.5 + 0.5)
    assertEquals(expected, f1, 0)
  }

  @Test
  def testAvgPrec(): Unit = {
    val falseNeg = avgPrecCorrectResults.count(r => !avgPrecResults.contains(r))
    //println("false neg: " + falseNeg)
    val AP = ScoringResources.avgPrec(avgPrecResults, avgPrecCorrectResults, falseNeg)
    val expected = (1.0 + 0.0 + 2.0/3.0 + 3.0/4.0 + 0.0) / 5.0
    assertEquals(expected, AP, 0)
  }

  @Test
  def testAvgPrecShortResults(): Unit = {
    val falseNeg = avgPrecCorrectResults.count(r => !avgPrecShortResults.contains(r))
    //println("false neg: " + falseNeg)
    val AP = ScoringResources.avgPrec(avgPrecShortResults, avgPrecCorrectResults, falseNeg)
    val expected = (1.0 + 0.0 + 2.0/3.0 + 3.0/4.0 + 0.0) / 4.0
    assertEquals(expected, AP, 0)
  }

  @Test
  def testMeanAvgPrec(): Unit = {
    val scoresList = List(new Scores(0.0, 0.0, 0.0, 0.4), new Scores(0.0, 0.0, 0.0, 0.5))
    val MAP = ScoringResources.meanAvgPrec(scoresList)
    val expected = 0.9 / 2.0
    assertEquals(expected, MAP, 0)
  }

  @Test
  def testGetScoresFromResults(): Unit = {
    val scores = ScoringResources.getScoresFromResults(results, correctResults)
    val p = 0.5
    val r = 0.5
    val f1 = 2 * (0.5 * 0.5) / (0.5 + 0.5)
    val AP = 2.0 / (2.0 + 2.0)
    assertEquals(p, scores.precision, 0)
    assertEquals(r, scores.recall, 0)
    assertEquals(f1, scores.f1, 0)
    assertEquals(AP, scores.avgPrecision, 0)
  }


}
