package edu.washington.cs.dericp

/**
  * Created by erikawolfe on 12/3/16.
  */
object Utils {
  def precision(truePos: Int, falsePos: Int): Double = truePos.toDouble / (truePos + falsePos)

  def recall(truePos: Int, falseNeg: Int): Double = truePos.toDouble / (truePos + falseNeg)

  def f1Score(truePos: Int, falsePos: Int, falseNeg: Int): Double = {
    val p = precision(truePos, falsePos)
    val r = recall(truePos, falseNeg)
    2 * p * r / (p + r)
  }

  def meanAvgPrec(avgPrecList: List[Double]) = avgPrecList.sum / avgPrecList.length

  // TODO: Decide if this should be implemented.  Seems easier / more
  // TODO: efficient to compute while finding query results
  // TODO: (!!) REMEMBER TO BOUND THE DENOMINATOR MIN( (TP + FN), 100)
  // def avgPrec(): Unit = {}
}
