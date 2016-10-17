package com.kadekm.scraper.htmlunit

import com.gargoylesoftware.htmlunit.{WebWindow => HtmlUnitWebWindow, html => htmlunit}
import com.kadekm.scraper._

import scala.collection.convert.WrapAsScala._

final case class HtmlUnitElement(underlying: htmlunit.DomElement) extends Element {
  override def tagName: String = underlying.getTagName

  override def text: String = underlying.getTextContent.trim

  override def children: Iterable[Element] = underlying.getChildElements.map(HtmlUnitElement)
}

final case class HtmlUnitDocument(underlying: HtmlUnitWebWindow) extends Document {
  override def location: String = ???

  override def root: Element = ???
}
