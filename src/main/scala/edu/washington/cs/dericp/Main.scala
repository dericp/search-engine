package edu.washington.cs.dericp

import scala.io.StdIn
import ch.ethz.dal.tinyir.io.TipsterStream
import com.github.aztek.porterstemmer.PorterStemmer

object Main {

  def main(args: Array[String]): Unit = {
    println("Which relevance model would you like to use? LANGUAGE or TERM:")
    val model = StdIn.readLine().toLowerCase
    println("Using " + model + " model.")
    println()

    println("Please enter your query:")
    val query = StdIn.readLine().split("\\s+").map(term => PorterStemmer.stem(term))
    println()

    println("Build a new inverted index from scratch? TRUE/FALSE:")
    val newIndex = StdIn.readLine().toBoolean
    println()

    println("Building inverted index...")

    val invIdx = {
      if (newIndex) {
        InvertedIndex.createInvertedIndex("src/main/resources/documents")
      } else {
        InvertedIndex.readInvertedIndexFromFile("src/main/resources/inverted-index.txt")
      }
    }

    // TODO: figure out the other case?
    val retrievalModel = model match {
      case "language" => new LanguageModel(invIdx, Map.empty, .01)
      case "term" => new TermModel(invIdx, Map.empty)
      case _ => new LanguageModel(invIdx, Map.empty, .01)
    }

    var keepQuerying = true

    while (keepQuerying) {
      // TODO: might want to delete query option from above
      println("Please enter your query:")
      val query = StdIn.readLine().split("\\s+").map(term => PorterStemmer.stem(term)).toList
      println()

      // COMPUTE WITH MODEL AND PRINT RESULTS
      val results = retrievalModel.topNDocs(query, 100)
      println(results)
      println()

      println("Would you like to enter another query? y/n:")
      val response = StdIn.readLine()//.startsWith("y")
      if (response.startsWith("n")) {
        keepQuerying = false
      }
    }

    // TODO: score documents

  }
}

