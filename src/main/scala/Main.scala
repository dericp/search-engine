/**
  * Created by dericp on 11/23/16.
  */
import ch.ethz.dal.tinyir.io.ReutersRCVStream
object Main {

  def main(args: Array[String]): Unit = {
    def docs = new ReutersRCVStream("src/main/resources/documents").stream

    val a = docs
  }

}
