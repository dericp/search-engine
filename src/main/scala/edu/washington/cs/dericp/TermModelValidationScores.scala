package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import scala.io.StdIn

/**
  * Created by erikawolfe on 12/12/16.
  */
object TermModelValidationScores {

  val INV_IDX_FILEPATH = "src/main/resources/inverted-index-tf-min-2"

  def main(args: Array[String]): Unit = {

    val invIdx = InvertedIndex.readInvertedIndexFromFile(INV_IDX_FILEPATH)
    println("inverted index done")
    def docs = new TipsterStream("src/main/resources/documents").stream
    val docLengths = docs.map(doc => doc.name -> doc.tokens.length).toMap

    println("doclengths done - starting queries")

    val tm = new TermModel(invIdx, docLengths)
    val queryNumToScores = ScoringUtils.computeAllScores(tm)
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
