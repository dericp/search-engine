package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import scala.io.StdIn

/**
  * Created by erikawolfe on 12/13/16.
  *
  * Class to print the validation scores from a term model or language model.
  * Takes the filepath and the model type from the user.
  *
  */
object PrintModelValidationScores {
  def printScores(): Unit = {
    println("What is the filepath for the inverted index file?")
    val filepath = StdIn.readLine()
    println()
    println("building inverted index...")

    val invIdx = InvertedIndex.readInvertedIndexFromFile(filepath)
    def docs = new TipsterStream("src/main/resources/documents").stream
    val docLengths = docs.map(doc => doc.name -> doc.tokens.length).toMap

    println()
    println("Do you want to use a language model or term model?  (LANG / TERM)")
    val model = StdIn.readLine().toLowerCase()

    val relevanceModel = model match {
      case "language" => new LanguageModel(invIdx, docLengths)
      case "term" => new TermModel(invIdx, docLengths)
      case _ => throw new IllegalArgumentException("Invalid relevance model name. Please enter one of the options.")
    }

    val queryNumToScores = ScoringUtils.computeAllScores(relevanceModel)
    val meanAvgPrec = ScoringUtils.meanAvgPrec(queryNumToScores.values.toSeq)

    for ((queryNum, scores) <- queryNumToScores) {
      println("Query Number: " + queryNum)
      println("Precision: " + scores.precision)
      println("Recall: " + scores.recall)
      println("F1: " + scores.f1)
      println("Average Precision: " + scores.avgPrecision)
    }
    print("Mean Average Precision: " + meanAvgPrec)
    println()
  }
}

