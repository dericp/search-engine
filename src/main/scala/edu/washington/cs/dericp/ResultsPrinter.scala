package edu.washington.cs.dericp

import java.io.PrintStream

import ch.ethz.dal.tinyir.io.TipsterStream

import scala.collection.immutable.ListMap
import scala.io.{Source, StdIn}

/**
  * Created by Isa on 12/12/2016.
  */
object ResultsPrinter {

  val INV_IDX_FILEPATH = "inverted-index-tf-min-2"
  val CUSTOM_RUN_TAG = ""

  /**
    * Reads in the test queries from the given file and returns them as a map from query ID to formatted query
    *
    * @return test queries
    */
  def getTestQueries(): Map[Int, Seq[String]] = {
    val lines = Source.fromFile("src/main/resources/test-questions.txt").getLines.toVector

    val numLines = lines.filter(line => line.startsWith("<num>")).map(_.substring(14).trim.toInt)
    val titleLines = lines.filter(line => line.startsWith("<title>")).map(_.substring(15).trim)
    numLines.zip(titleLines).toMap.mapValues(q => Utils.getQueryTermsFromString(q))
  }

  /**
    * Prints the results of a query search to text file
    *
    * @param results
    * @param ps
    */
  def printResults(results: Map[Int, Seq[(String, Int)]], ps: PrintStream): Unit = {
    results.foreach{ case (qNum, docList) => docList.foreach(dwi => printLineToFile(qNum, dwi, ps))}
    ps.close()
  }

  /**
    * Prints a specific line of results to the text file
    *
    * @param qNum
    * @param docWithIndex
    * @param ps
    */
  def printLineToFile(qNum: Int, docWithIndex: (String, Int), ps: PrintStream): Unit = {
    ps.println(qNum + " " + (docWithIndex._2 + 1) + " " + docWithIndex._1)
  }

  /**
    * Runs the full process of creating an invertedIndex, a relevance model and find results for the set of
    * test queries. It then prints the results to a text file.
    *
    * @param invertedIndex
    * @param model term model or language model
    * @param modelLabel: "l" if model is a language model, "t" if it is a term model
    * @param docLengths: map from doc ID to doc length
    */
  def createResultsFile(invertedIndex: Map[String, Seq[DocData]], model: RelevanceModel, modelLabel: Char, docLengths: Map[String, Int]): Unit = {
    println("Creating results file")
    val queries = ListMap(getTestQueries().toSeq.sortBy(_._1):_*)
    val results = ScoringUtils.getRelevanceModelResults(model, queries).mapValues(_.zipWithIndex)
    val ps = new PrintStream("ranking-" + modelLabel + "-13.run")
    printResults(results, ps)
    println("Results file created")
  }
}
