package edu.washington.cs.dericp
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.{Document, XMLDocument}

/**
  * Created by Isa on 11/27/2016.
  */


object Main {

  /*def listIntersection(query: List[String], index: Map[String, List[(String, Int)]]) = {
    // create output map, trimmed inverted index, and index counter
    def output = scala.collection.mutable.Seq.empty
    val queryIndex: Map[String, Vector[String]] = index.filter{case(term, _) => query.contains(term)}.mapValues(l => l.map(_._1).to[Vector])//_.to[Vector])
    val counter = collection.mutable.Map() ++ queryIndex.mapValues(_ => 0)

    // see if we have reached the end of a term's posting list
    var keepSearching = true

    // increment the index for a term in counter, check if we have reached end of posting list
    def incrIndex(term: String): Unit = {
      val newIndex = counter(term) + 1
      if (newIndex < queryIndex(term).size) {
        counter(term) = newIndex
      } else {
        keepSearching = false
      }
    }

    // continuously search for intersections
    while (keepSearching) {
      // look at the current doc for each term
      val termToCurrentDoc = counter.map{ case(term, index) => (term, queryIndex(term)(index)) }
      val lowestTerm = termToCurrentDoc.reduceLeft(min)
      val highestTermDoc = termToCurrentDoc(termToCurrentDoc.reduceLeft(max))
      // if lowest doc == highest doc, we found intersection, otherwise increment the lowest doc and keep searching
      if (termToCurrentDoc(lowestTerm).equals(highestTermDoc)) {
        output ++ highestTermDoc
        counter.foreach{case (term, _) => incrIndex(term)}
      } else {
        incrIndex(lowestTerm)
      }
    }
  }

  // find min based on doc ID (_2) and return corresponding term
  def min(a: (String, String), b: (String, String)) : String = {
    if (a._2 < b._2) a._1
    else b._1
  }

  // find max based on doc ID (_2) and return corresponding term
  def max(a: (String, String), b: (String, String)) : String = {
    if (a._2 > b._2) a._1
    else b._1
  }*/

  def main(args: Array[String]): Unit = {
    // TODO: Use the whole document stream

    val docs = new TipsterStream ("src/main/resources/documents").stream.take(10)
    val invInd1 = InvertedIndex.invertedIndex(docs)
    println("finished building index")
    println(invInd1)
    // InvertedIndex.printIndexToFile(invInd1)

    // val invInd2 = InvertedIndex.readIndexFromFile("src/main/resources/inverted-index.txt")
    // println(invInd1)
    // println(invInd2)
    // println("hiii")


    ///////// INVERTED INDEX USING TERM MODEL TESTING
    //    val docLengths = docs.map(doc => (doc.name -> doc.tokens.length)).toMap
//    val termModel = new TermModel(invInd, docLengths)
//
//    // common words: society, cages, 000
//    val doc1 = docs(0)
//    // common words: his, george, hampshire, dole, buckley
//    val doc2 = docs(1)
//    println("doc1 name: " + doc1.name)
//
//    // val query = List("his", "hampshire", "dole", "buckley")
//    val query = List("society", "cages", "000")
//
//    // println("score 1 (should be low): " + termModel.tfIdfScore(query, doc1.name))
//    // println("score 2 (should be higher): " + termModel.tfIdfScore(query, doc2.name))
//
//    println(termModel.topNDocs(query, 15))

  }
}

