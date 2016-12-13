package edu.washington.cs.dericp

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

}
