package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream

import scala.io.StdIn

/**
  * This is for tuning the lambda value in the language model.
  */
object LanguageModelValidationScores {

  val INV_IDX_FILEPATH = "src/main/resources/inverted-index-tf-min-2"

  def main(args: Array[String]): Unit = {

    val invIdx = InvertedIndex.readInvertedIndexFromFile(INV_IDX_FILEPATH)
    def docs = new TipsterStream("src/main/resources/documents").stream
    val docLengths = docs.map(doc => doc.name -> doc.tokens.length).toMap

    var keepGoing = true

    while (keepGoing) {
      println("What lambda value do you want to use?")
      val lambda = StdIn.readDouble()
      println("running with lambda value " + lambda)

      val lm = new LanguageModel(invIdx, docLengths, lambda)

      val queryNumToScores = ScoringUtils.computeAllScores(lm)
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

      println("keep going?")
      keepGoing = StdIn.readBoolean()
    }
  }

}
