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

    val b = InvertedIndex.readInvertedIndexFromFile("src/main/resources/inverted-index")

    val c = mutable.Map.empty[String, Int]
    for ((term, l) <- b) {
      for (dd <- l) {
        c(dd.id()) = c.getOrElse(dd.id(), 0) + dd.freq
      }
    }

    // top doc = AP880215-0217
    val q = List("Denmark", "women", "military", "pilot").map(term => PorterStemmer.stem(term))

    //println("finding listintersection")
    //println(InvertedIndex.listIntersection(q, b))

    //println(b(PorterStemmer.stem("society")).toString)
    println("creating langmodel")

    val lm = new LanguageModel(b, c.toMap)

    println("done creating langmodel")

    println(lm.topNDocs(q, 3, 0.05).toString)

  }

}
