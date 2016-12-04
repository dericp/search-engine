package edu.washington.cs.dericp

import breeze.linalg.DenseVector
import ch.ethz.dal.tinyir.processing.{Document, XMLDocument}

import scala.io.Source

/**
  * Created by erikawolfe on 12/3/16.
  */
object Utils {
  val STOP_WORDS = Source.fromFile("src/main/resources/stop-words.txt").getLines.toSet

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

  def pruneStopWords(termFreq: Map[String, Int]): Map[String, Int] = {
    termFreq.filterKeys(!STOP_WORDS.contains(_))
  }

  def getTermFrequencies(doc: XMLDocument): Map[String, Int] = {
    Utils.pruneStopWords(doc.tokens.groupBy(identity).mapValues(termList => termList.size))
  }

  def getCodes(): Set[String] = {
    val industryCodes = Source.fromFile("src/main/resources/codes/industry_codes.txt").getLines().map(line => line.split("\t")(0)).toSet
    val regionCodes = Source.fromFile("src/main/resources/codes/region_codes.txt").getLines().map(line => line.split("\t")(0)).toSet
    val topicCodes = Source.fromFile("src/main/resources/codes/topic_codes.txt").getLines().map(line => line.split("\t")(0)).toSet
    industryCodes union regionCodes union topicCodes
  }

  def getTopCodes(docs: Stream[XMLDocument], numCodes: Int): Set[String] = {
    docs.flatMap(_.codes).groupBy(identity).mapValues(l => l.length).toSeq.sortBy(-_._2).take(numCodes).map(_._1).toSet
  }

  // Takes an XMLDocument
  // Returns the term frequency map of the document, deleting stop words and keeping only the top k
  def topKTermFreq(doc: XMLDocument, k: Int): Map[String, Int] = {
    collection.immutable.ListMap(getTermFrequencies(doc).toList.sortBy{-_._2}:_*).take(k)
  }

  def getTopTerms(docs: Stream[XMLDocument], numTerms: Int): Set[String] = {
    pruneStopWords(docs.flatMap(_.tokens).groupBy(identity).mapValues(l => l.size)).toSeq.sortBy(-_._2).take(numTerms).map((t) => t._1).toSet
  }

  def getFeatureVector(docTermFreq: Map[String, Int], termToIndexInFeatureVector: Map[String, Int], dimensions: Int): DenseVector[Double] = {
    val featureVector = DenseVector.zeros[Double](dimensions)
    for ((term, freq) <- docTermFreq) {
      featureVector(termToIndexInFeatureVector(term)) = freq.toDouble
    }
    //docTermFreq.foreach { case (term, freq) => emptyFeatureVector(termToIndexInFeatureVector.get(term).get) = freq.toDouble }
    //emptyFeatureVector
    featureVector
  }
}
