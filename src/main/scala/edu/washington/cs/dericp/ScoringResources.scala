package edu.washington.cs.dericp

import java.io.{File, PrintStream, Serializable}
import java.util.Scanner

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by erikawolfe on 12/6/16.
  */
object ScoringResources {

  case class Scores(precision: Double, recall: Double, f1: Double, avgPrecision: Double)

  def getScoresFromResults(results: List[String], correctResults: Set[String]): Scores = {
    val truePos = results.toSet.intersect(correctResults).size
    val falsePos = results.size - truePos
    val falseNeg = correctResults.size - truePos
    val p = precision(truePos, falsePos)
    val r = recall(truePos, falseNeg)
    val f1 = f1Score(p, r)
    val ap = avgPrec(results, correctResults, falseNeg)
    return new Scores(p, r, f1, ap)
  }

  def getScoresFromResults(queryNum: Int, results: List[String]): Scores = {
    val correctResults = getCorrectResults.getOrElse(queryNum, List[String]()).toSet
    getScoresFromResults(results, correctResults)
  }

  def precision(truePos: Int, falsePos: Int): Double = truePos.toDouble / (truePos + falsePos)

  def recall(truePos: Int, falseNeg: Int): Double = truePos.toDouble / (truePos + falseNeg)

  def f1Score(p: Double, r: Double): Double = { 2 * p * r / (p + r) }

  def f1Score(truePos: Int, falsePos: Int, falseNeg: Int): Double = {
    val p = precision(truePos, falsePos)
    val r = recall(truePos, falseNeg)
    2 * p * r / (p + r)
  }

  // TODO: (!!) REMEMBER TO BOUND THE DENOMINATOR MIN( (TP + FN), 100)
  // AP = (SUM (Precision at rank k * (1 if doc is relevant, 0 otherwise))) / MIN(truePos + falseNeg, correctResults.size)
  def avgPrec(results: List[String], correctResults: Set[String], falseNeg: Int): Double = {
    val correctResultsSet = correctResults.toSet
    var precisionSum = 0.0
    var truePos = 0
    var falsePos = 0

    for (i <- results.indices ) {
      if (correctResultsSet.contains(results(i))) {
        truePos += 1
        precisionSum += precision(truePos, falsePos)
      } else {
        falsePos += 1
      }
    }
    precisionSum / Math.min(truePos + falseNeg, correctResults.size).toDouble
  }

  def meanAvgPrec(avgPrecList: List[Double]) = avgPrecList.sum / avgPrecList.length

  def meanAvgPrecComplex(scoresList: List[Scores]): Double = {
    meanAvgPrec(scoresList.map(_.avgPrecision))
  }


  // Returns the queries to be used to test the scoring algorithms
  // In a ListBuffer of (Int, String) meaning (query #, query)
  def getQueries: List[(Int, String)] = {
    val queryPairs = new ListBuffer[(Int, String)]
    val input = new Scanner(new File("src/main/resources/simple-questions-descriptions.txt"))
    while(input.hasNextLine) {
      val num = input.nextLine.toInt
      val query = input.nextLine()
      queryPairs.+=((num, query))
    }
    queryPairs.toList
  }

  // Returns the correct documents for each query
  // In a map from query number --> Set of correct documents
  def getCorrectResults: scala.collection.Map[Int, List[String]] = {
    val lines = Source.fromFile("src/main/resources/simple-relevance-judgements.txt").getLines()
    val results = new collection.mutable.HashMap[Int, ListBuffer[String]]

    def addResult(line: String): Unit = {
      if (line != "") {
        val key = line.substring(0, 2).toInt
        val value = line.substring(3)
        if (!results.contains(key)) {
          results(key) = new ListBuffer[String]
        }
        results(key).+=(value)
      }
    }
    lines.foreach(addResult(_))
    results.mapValues(l => l.toList)
  }

  // creates a simplified version of "question-descriptions.txt" that has just
  // each query number and each query, all on their own line
  // stored as "simple-questions-descriptions.txt"
  def createShortenedQueryFile(): Unit = {
    def shortenLine(line: String): String = {
      if (line.startsWith("<num>")) {
        line.substring(14).trim
      } else if (line.startsWith("<title>")) {
        line.substring(15).trim
      } else {
        throw new IllegalArgumentException("only lines beginning with <num> or <title> can be passed to shortenLine")
      }
    }

    val lines = Source.fromFile("src/main/resources/questions-descriptions.txt").getLines.toList.
      filter(line => line.startsWith("<title>") || line.startsWith("<num>"))
    val shortenedLines = lines.map(shortenLine(_))
    val ps = new PrintStream("src/main/resources/simple-questions-descriptions.txt")
    shortenedLines.map(ps.println(_))
    ps.close()
  }

  // creates a simplified version of "relevance-judgements.csv" that has just
  // [topic] [doc id]
  // for the documents that are relevant to the topic
  def createShortenedJudgementFile(): Unit = {
    val ps = new PrintStream("src/main/resources/simple-relevance-judgements.txt")
    val input = new Scanner(new File("src/main/resources/relevance-judgements.csv"))

    while (input.hasNextLine()) {
      // format: 51 0 FR891103-0032 1
      val line = input.nextLine().split("\\s+")

      if (line.length > 3 && line(3) == "1") {
        val topic = line(0)
        val id = line(2)
        ps.println(topic + " " + id)
      }
    }
  }
}
