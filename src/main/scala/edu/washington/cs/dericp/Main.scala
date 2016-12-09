package edu.washington.cs.dericp

import scala.io.StdIn
import ch.ethz.dal.tinyir.io.TipsterStream

object Main {

  def main(args: Array[String]): Unit = {
    println("Which relevance model would you like to use? LANGUAGE or TERM:")
    val model = StdIn.readLine()
    println("Using " + model + " model.")
    println()

    println("Please enter your query:")
    val query = StdIn.readLine().split("\\s+")
    println()

    println("Build a new inverted index from scratch? y/n:")
    val newIndex = StdIn.readLine().toBoolean

    val invIdx = {
      if (newIndex) {
        val docs = new TipsterStream ("src/main/resources/documents").stream//.take(10)
        InvertedIndex.createInvertedIndex(docs)
      } else {
        InvertedIndex.readInvertedIndexFromFile("src/main/resources/inverted-index.txt")
      }
    }

    // TODO: score documents and return top n
  }
}

