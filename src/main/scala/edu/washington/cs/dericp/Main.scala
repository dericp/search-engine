package edu.washington.cs.dericp
import java.io.PrintStream
import java.util.Scanner

import scala.io.Source
import java.io.File
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.{Document, XMLDocument}

/**
  * Created by Isa on 11/27/2016.
  */

case class TfTuple(term: String, doc: Int, count: Int)

object Main {
//    def tfTuples (docs: Stream[Document]) : Stream[TfTuple] =
//      docs.flatMap( d => d.tokens.groupBy(identity)
//        .map{ case (tk,lst) => TfTuple(tk, d.ID, lst.length) }
//
//    val fqIndex : Map[String,List[(Int,Int)]] =
//      tfTuples(docs).groupBy(_.term)
//        .mapValues(_.map(tfT => (tfT.doc, tfT.count)).sorted

  def main(args: Array[String]): Unit = {
    createShortenedJudgementFile

    ///////// INVERTED INDEX USING TERM MODEL TESTING
    // TODO: Use the whole document stream
//    val docs = new TipsterStream ("src/main/resources/documents").stream.take(1000)
//    val invInd = new InvertedIndex(docs).invertedIndex
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

  // creates a simplified version of "question-descriptions.txt" that has just
  // each query number and each query, all on their own line
  // stored as "simple-questions-descriptions.txt"
  def createShortenedQueryFile: Unit = {
    def shortenLine(line: String): String = {
      if (line.startsWith("<num>")) {
        line.substring(14).trim
      } else if (line.startsWith("<title>")) {
        line.substring(15).trim
      } else {
        throw new IllegalArgumentException("only lines beginning with <num> or <title> can be passed to shortenLine")
      }
    }

    val lines = Source.fromFile("src/main/resources/questions-descriptions.txt").getLines.toList.filter(line => line.startsWith("<title>") || line.startsWith("<num>"))
    val shortenedLines = lines.map(shortenLine(_))
    val ps = new PrintStream("src/main/resources/simple-questions-descriptions.txt")
    shortenedLines.map(ps.println(_))
    ps.close()
  }

  // creates a simplified version of "relevance-judgements.csv" that has just
  // [topic] [doc id]
  // for the documents that are relevant to the topic
  def createShortenedJudgementFile: Unit = {
    val ps = new PrintStream("src/main/resources/simple-relevance-judgements.txt")
    val input = new Scanner(new File("src/main/resources/relevance-judgements.csv"))

    while(input.hasNextLine()) {
      // format: 51 0 FR891103-0032 1
      val line = input.nextLine().split("\\s+")

      if (line.length > 3 && line(3) == "1") {
        val topic = line(0)
        val id = line(2)
        ps.println(topic + " " + id)
      }
    }
  }
}

