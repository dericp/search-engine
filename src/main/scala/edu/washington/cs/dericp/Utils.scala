package edu.washington.cs.dericp

import breeze.linalg.DenseVector
import ch.ethz.dal.tinyir.processing.XMLDocument
import com.github.aztek.porterstemmer.PorterStemmer

import scala.io.Source

object Utils {
  /**
    * A set of all stop words to filter from the documents and queries
    */
  val STOP_WORDS = Source.fromFile("src/main/resources/stop-words.txt").getLines.toSet

  /**
    * Formats a single string query into a sequence of terms that have been stemmed, set to lower case, and
    * had all non word characters and stop words removed
    *
    * @param query
    * @return formatted query
    */
  def getQueryTermsFromString(query: String): Seq[String] = {
    query.toLowerCase.replaceAll("[^a-z0-9.]", " ").split("\\s+")
      .filter(term => !term.isEmpty && !STOP_WORDS.contains(term))
      .map(term => PorterStemmer.stem(term))
  }




  ///////////////////////////////////////////////////////////
  // Move methods above this line when you use them!
  ///////////////////////////////////////////////////////////




  def pruneStopWordsFromTF(termFreq: Map[String, Int]): Map[String, Int] = {
    termFreq.filterKeys(!STOP_WORDS.contains(_))
  }

  def getTermFrequencies(doc: XMLDocument): Map[String, Int] = {
    Utils.pruneStopWordsFromTF(doc.tokens.groupBy(identity).mapValues(termList => termList.size))
  }

  // Takes an XMLDocument
  // Returns the term frequency map of the document, deleting stop words and keeping only the top k
  def topKTermFreq(doc: XMLDocument, k: Int): Map[String, Int] = {
    collection.immutable.ListMap(getTermFrequencies(doc).toList.sortBy{-_._2}:_*).take(k)
  }

  def getTopTerms(docs: Stream[XMLDocument], numTerms: Int): Set[String] = {
    pruneStopWordsFromTF(docs.flatMap(_.tokens).groupBy(identity).mapValues(l => l.size)).toSeq.sortBy(-_._2).take(numTerms).map((t) => t._1).toSet
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
