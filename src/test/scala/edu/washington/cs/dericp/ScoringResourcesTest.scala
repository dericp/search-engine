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
    val falseNeg = correctResults.count(r => results.contains(r))
    val AP = ScoringResources.avgPrec(results, correctResults, falseNeg)
    val expected = 2.0 / (2.0 + falseNeg)
    assertEquals(expected, AP, 0)
  }

  @Test
  def testMeanAvgPrec(): Unit = {
    val avgPrecList = List(0.4, 0.5, 0.3)
    val MAP = ScoringResources.meanAvgPrec(avgPrecList)
    assertEquals(1.2/3.0, MAP, 0)
  }

  @Test
  def testMeanAvgPrecComplex(): Unit = {
    val scoresList = List(new Scores(0.0, 0.0, 0.0, 0.4), new Scores(0.0, 0.0, 0.0, 0.5))
    val MAP = ScoringResources.meanAvgPrecComplex(scoresList)
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
