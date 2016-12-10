package edu.washington.cs.dericp

import scala.io.StdIn
import ch.ethz.dal.tinyir.io.TipsterStream
import com.github.aztek.porterstemmer.PorterStemmer

object Main {

  def main(args: Array[String]): Unit = {
    println("Which relevance model would you like to use? LANGUAGE or TERM:")
    val model = StdIn.readLine()
    println("Using " + model + " model.")
    println()

    println("Please enter your query:")
    val query = StdIn.readLine().split("\\s+").map(term => PorterStemmer.stem(term))
    println()

    println("Build a new inverted index from scratch? y/n:")
    val newIndex = StdIn.readLine().toBoolean

    val invIdx = {
      if (newIndex) {
        InvertedIndex.createInvertedIndex("src/main/resources/documents")
      } else {
        InvertedIndex.readInvertedIndexFromFile("src/main/resources/inverted-index.txt")
      }
    }

    // TODO: score documents and return top n
  }
}

