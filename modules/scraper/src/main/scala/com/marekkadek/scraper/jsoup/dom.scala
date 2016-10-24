package com.marekkadek.scraper.jsoup

import com.marekkadek.scraper.{Document, Element}

import scala.collection.convert.WrapAsScala._

final case class JsoupElement(underlying: org.jsoup.nodes.Element) extends Element {
  override def select(query: String): Iterable[Element] =
    underlying.select(query).toIterable.map(JsoupElement)

  override def tagName: String = underlying.tagName

  override def text: String = underlying.text

  override def children: Iterable[Element] = underlying.children.toIterable.map(JsoupElement)

  override def attr(name: String): Option[String] =
    if (underlying.hasAttr(name)) Some(underlying.attr(name)) else None
}

final case class JsoupDocument(underlying: org.jsoup.nodes.Document) extends Document {
  override def location: String = underlying.location

  override def root: Element = JsoupElement(underlying.getElementsByTag("html").first)
}
