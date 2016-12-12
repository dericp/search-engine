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

    println("Build a new inverted index from scratch? TRUE/FALSE:")
    val newIndex = StdIn.readLine().toBoolean
    println("Building inverted index...")
    println()

    val invIdx = {
      if (newIndex) {
        InvertedIndex.createInvertedIndex("src/main/resources/documents")
      } else {
        println("What is the relative filepath of the saved inverted index?")
        val invIdxFilepath = StdIn.readLine()
        println("Loading inverted index from " + invIdxFilepath + "...")
        InvertedIndex.readInvertedIndexFromFile(invIdxFilepath)
      }
    }

    if (newIndex) {
      println("Would you like to save this inverted index to a file? TRUE/FALSE:")
      if (StdIn.readLine().toBoolean) {
        println("What is the relative filepath you would like to save this inverted index to?")
        val filepath = StdIn.readLine()
        println("Saving inverted index to " + filepath + "...")
        InvertedIndex.writeInvertedIndexToFile(invIdx, filepath)
      }
    }
    println()

    println("Building relevance model...")

    // get the document lengths
    def docs = new TipsterStream("src/main/resources/documents").stream
    val docLengths = docs.map(doc => doc.name -> doc.tokens.length).toMap

    val relevanceModel = model match {
      case "language" => new LanguageModel(invIdx, docLengths, .01)
      case "term" => new TermModel(invIdx, docLengths)
      case _ => throw new IllegalArgumentException("Invalid relevance model name. Please enter one of the options.")
    }

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

