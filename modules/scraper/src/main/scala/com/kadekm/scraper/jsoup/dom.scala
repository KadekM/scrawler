package com.kadekm.scraper.jsoup
import com.kadekm.scraper._

import scala.collection.convert.WrapAsScala._

final case class JsoupElement(underlying: org.jsoup.nodes.Element) extends Element {
  override def tagName: String = underlying.tagName

  override def text: String = underlying.text

  override def children: Iterable[Element] = underlying.children.toIterable.map(JsoupElement)
}

final case class JsoupDocument(underlying: org.jsoup.nodes.Document) extends Document {
  override def location: String = underlying.location

  override def root: Element = JsoupElement(underlying.getElementsByTag("html").first)
}
