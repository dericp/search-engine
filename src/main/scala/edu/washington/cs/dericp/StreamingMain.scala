package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import com.github.aztek.porterstemmer.PorterStemmer

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

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


  def main(args: Array[String]): Unit = {
    def docs = new TipsterStream(FILEPATH).stream

    val n = 100


    var keepQuerying = true

    while (keepQuerying) {
      println()
      println("Please enter your query:")
      val query = Utils.getQueryTermsFromString(StdIn.readLine())
      println()


      val results = ListBuffer.empty[(String, Double)]
      for (d <- docs) {
        val tokens = d.tokens
        results += ((d.name, query.map(q => findPWD(tokens.count(_ == q), tokens.length)).sum))
      }

      val out = results.toSeq.sortBy(-_._2).take(n)

      println(out.mkString(", "))

      println()
      println("Would you like to enter another query? TRUE/FALSE:")
      keepQuerying = StdIn.readLine().toBoolean
    }

  }

}
