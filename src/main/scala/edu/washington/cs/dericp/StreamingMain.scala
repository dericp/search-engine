package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import com.github.aztek.porterstemmer.PorterStemmer

import scala.io.StdIn
import scala.math._

/**
  * Purpose of this class is to find results without an Inverted Index and instead pass every doc sequentially per query
  * This is useful to compare times
  */
object StreamingMain {

  val FILEPATH = "src/main/resources/documents"


  //SUPER NAIVE
  def findPWD(tf: Int, doclength: Int) : Double = {
    tf.toDouble / doclength.toDouble
  }


  def main(): Unit = {
    def docs = new TipsterStream(FILEPATH).stream

    def docsToTerms = docs.map(doc => (doc.name, doc.tokens.filter(!Utils.STOP_WORDS.contains(_)).map(token => (PorterStemmer.stem(token))))).toMap
    val docsToLength = docsToTerms.mapValues(_.length)
    val docsToTF = docsToTerms.mapValues(_.groupBy(identity).mapValues(_.length))
    val n = 100


    var keepQuerying = true

    while (keepQuerying) {
      println()
      println("Please enter your query:")
      val query = Utils.getQueryTermsFromString(StdIn.readLine())
      println()


      val logPQDs = docsToTerms.keys.map(doc => (doc, query.map(q => log(findPWD(docsToTF(doc)(q), docsToLength(doc)))).sum)).toSeq
      val results = logPQDs.sortBy(-_._2).take(n)

      println(results.mkString(", "))

      println()
      println("Would you like to enter another query? TRUE/FALSE:")
      keepQuerying = StdIn.readLine().toBoolean
    }

  }

}
