package edu.washington.cs.dericp

import java.io.PrintStream

import ch.ethz.dal.tinyir.io.TipsterStream

import scala.io.Source

/**
  * Created by Isa on 12/12/2016.
  */
object ResultsPrinter {

  val INV_IDX_FILEPATH = "inverted-index-tf-min-2"
  val CUSTOM_RUN_TAG = ""

  def getTestQueries(): Map[Int, Seq[String]] = {
    val lines = Source.fromFile("src/main/resources/test-questions.txt").getLines.toVector

    val numLines = lines.filter(line => line.startsWith("<num>")).map(_.substring(14).trim.toInt)
    val titleLines = lines.filter(line => line.startsWith("<title>")).map(_.substring(15).trim)
    numLines.zip(titleLines).toMap.mapValues(q => Utils.getQueryTermsFromString(q))
  }

  def printResults(results: Map[Int, Seq[(String, Int)]], ps: PrintStream): Unit = {
    results.foreach{ case (qNum, docList) => docList.foreach(dwi => printToFile(qNum, dwi, ps))}
    ps.close()
  }

  def printToFile(qNum: Int, docWithIndex: (String, Int), ps: PrintStream): Unit = {
    ps.println(qNum + " " + (docWithIndex._2 + 1) + " " + docWithIndex._1)
  }

  def main(args: Array[String]): Unit = {
    val queries = getTestQueries().take(1)
    val useLangModel = true

    val invIndex = InvertedIndex.readInvertedIndexFromFile(INV_IDX_FILEPATH)

    def docs = new TipsterStream("src/main/resources/documents").stream
    val docLengths = docs.map(doc => doc.name -> doc.tokens.length).toMap

    val lambda = .01

    if (useLangModel) {
      val lm = new LanguageModel(invIndex, docLengths, lambda)
      val results = ScoringResources.getRelevanceModelResults(lm, queries).mapValues(_.zipWithIndex)
      val ps = new PrintStream("ranking-l-13.run" + CUSTOM_RUN_TAG)
      printResults(results, ps)
    } else {
      val tm = new TermModel(invIndex, docLengths)
      val results = ScoringResources.getRelevanceModelResults(tm, queries).mapValues(_.zipWithIndex)
      val ps = new PrintStream("ranking-t-13.run" + CUSTOM_RUN_TAG)
      printResults(results, ps)
    }

  }

}
