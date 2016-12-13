package edu.washington.cs.dericp

import scala.io.StdIn
import ch.ethz.dal.tinyir.io.TipsterStream

object Main {

  def main(args: Array[String]): Unit = {
    println("Which relevance model would you like to use? LANGUAGE or TERM:")
    val model = StdIn.readLine().toLowerCase
    println("Using " + model + " model.")
    println()

    val invIdx = createInvertedIndex()

    println("Building relevance model...")

    // get the document lengths
    def docs = new TipsterStream("src/main/resources/documents").stream
    val docLengths = docs.map(doc => doc.name -> doc.tokens.length).toMap

    // create the relevance model
    val relevanceModel = model match {
      case "language" => new LanguageModel(invIdx, docLengths)
      case "term" => new TermModel(invIdx, docLengths)
      case _ => throw new IllegalArgumentException("Invalid relevance model name. Please enter one of the options.")
    }

    println("Would you like to manually enter queries or create a results file with the test queries?  (MANUAL / TEST)")
    val runType = StdIn.readLine().toLowerCase()

    runType match {
      case "manual" => queryLoop(relevanceModel)
      case "test" => ResultsPrinter.createResultsFile(invIdx, relevanceModel, model.charAt(0), docLengths)
    }
  }

  /**
    * Asks the user if they want to create a new inverted index or read it in from a file.
    * If the user creates a new inverted index, gives the option to write that index to file.
    *
    * @return an inverted index, either newly created or read from a given file path
    */
  def createInvertedIndex(): Map[String, Seq[DocData]] = {
    println("Build a new inverted index from scratch? TRUE/FALSE:")
    val newIndex = StdIn.readLine().toBoolean
    println()

    val invIdx = {
      if (newIndex) {
        println("Building inverted index...")
        InvertedIndex.createInvertedIndex("src/main/resources/documents")
      } else {
        println("What is the relative filepath of the saved inverted index?")
        val invIdxFilepath = StdIn.readLine()
        println()
        println("Loading inverted index from " + invIdxFilepath + "...")
        InvertedIndex.readInvertedIndexFromFile(invIdxFilepath)
      }
    }
    println()

    if (newIndex) {
      println("Would you like to save this inverted index to a file? TRUE/FALSE:")
      if (StdIn.readLine().toBoolean) {
        println("What is the relative filepath you would like to save this inverted index to?")
        val filepath = StdIn.readLine()
        println("Saving inverted index to " + filepath + "...")
        InvertedIndex.writeInvertedIndexToFile(invIdx, filepath)
      }
    }
    invIdx
  }

  /**
    * Allows the user to enter as many queries as they would like
    * Prints the top 100 documents for each query
    *
    * @param relevanceModel
    */
  def queryLoop(relevanceModel: RelevanceModel): Unit = {
    var keepQuerying = true

    while (keepQuerying) {
      println()
      println("Please enter your query:")
      val query = Utils.getQueryTermsFromString(StdIn.readLine())
      println()

      println("Getting top documents...")
      println(relevanceModel.topNDocs(query, 100).mkString(", "))

      println()
      println("Would you like to enter another query? TRUE/FALSE:")
      keepQuerying = StdIn.readLine().toBoolean
    }
  }
}

