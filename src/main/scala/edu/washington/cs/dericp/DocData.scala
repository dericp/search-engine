import ch.ethz.dal.tinyir.indexing.Result
import ch.ethz.dal.tinyir.processing.XMLDocument

class DocData(val doc : XMLDocument) extends Result[XMLDocument] {

  def id() : Int = {
    doc.ID
  }

  def matches(that : XMLDocument) : Int = {
    doc.ID - that.ID
  }

  def matched(that : XMLDocument) : XMLDocument = {
    doc
  }
}
