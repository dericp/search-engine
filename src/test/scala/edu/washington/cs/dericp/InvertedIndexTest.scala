package edu.washington.cs.dericp

import ch.ethz.dal.tinyir.io.TipsterStream
import com.github.aztek.porterstemmer.PorterStemmer

import scala.collection.mutable

/**
  * Created by dericp on 12/4/16.
  */
object InvertedIndexTest {

  def main(args: Array[String]): Unit = {

    //val a = InvertedIndex.createInvertedIndex("src/main/resources/documents")
    //println(ScoringResources.getQueries.toString)

    val b = InvertedIndex.readInvertedIndexFromFile("src/main/resources/inverted-index")

    val c = mutable.Map.empty[String, Int]
    for ((term, l) <- b) {
      for (dd <- l) {
        c(dd.id()) = c.getOrElse(dd.id(), 0) + dd.freq
      }
    }

    val lm = new LanguageModel(b, c.toMap, .01)

    val scores = ScoringResources.computeAllScores(lm)
    val MAP = ScoringResources.meanAvgPrec(scores.values.toSeq)

    println("MAP: " + MAP)

    /*
    // top doc = AP880215-0217
    //val q = List("Denmark", "women", "military", "pilot").map(term => PorterStemmer.stem(term))
    val q1 = List("Airbus", "subsidies").map(term => PorterStemmer.stem(term))
    val q2 = List("South", "African", "sanctions").map(term => PorterStemmer.stem(term))

    //println("finding listintersection")
    //println(InvertedIndex.listIntersection(q, b))

    //println(b(PorterStemmer.stem("society")).toString)
    println("creating langmodel")

    val lm = new LanguageModel(b, c.toMap, .01)

    println("done creating langmodel")

    val results1 = lm.topNDocs(q1, 100)
    //println(results1.toString)
    val scores1 = ScoringResources.getScoresFromResults(51, results1)
    println(scores1.precision)
    println(scores1.recall)
    println(scores1.f1)
    println(scores1.avgPrecision)
    println()

    val results2 = lm.topNDocs(q2, 100)
    println(results2.toString)
    val scores2 = ScoringResources.getScoresFromResults(52, results2)
    println(scores2.precision)
    println(scores2.recall)
    println(scores2.f1)
    println(scores2.avgPrecision)
    println()

    val scores = ScoringResources.computeScores(List((51, results1), (52, results2)).toMap)
//    println(scores(51).precision)
//    println(scores(51).recall)
//    println(scores(51).f1)
//    println(scores(51).avgPrecision)
//    println()
//
//    println(scores(52).precision)
//    println(scores(52).recall)
//    println(scores(52).f1)
//    println(scores(52).avgPrecision)
//    println()

    println(ScoringResources.meanAvgPrec(scores.values.toList))
    */
  }

}
