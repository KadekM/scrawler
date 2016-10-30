package com.marekkadek.scraper.htmlunit

import com.gargoylesoftware.htmlunit.html.{DomElement, HTMLParser}
import com.gargoylesoftware.htmlunit.{
  SgmlPage,
  StringWebResponse,
  TextPage,
  WebWindow => HtmlUnitWebWindow,
  html => htmlunit
}
import com.marekkadek.scraper.{Document, Element}

import scala.collection.convert.WrapAsScala._

final case class HtmlUnitElement(underlying: htmlunit.DomElement) extends Element {
  override def select(query: String): Iterable[Element] =
    underlying.querySelectorAll(query).collect {
      case e: DomElement => HtmlUnitElement(e)
    }

  override def tagName: String = underlying.getTagName

  override def text: String = underlying.getTextContent.trim

  override def children: Iterable[Element] = underlying.getChildElements.map(HtmlUnitElement)

  override def attr(name: String): Option[String] = {
    val att = underlying.getAttribute(name)
    if (att ne htmlunit.DomElement.ATTRIBUTE_NOT_DEFINED) Some(att)
    else None
  }
}

final case class HtmlUnitDocument(window: HtmlUnitWebWindow) extends Document {
  private[this] var _underlying: SgmlPage = null

  def underlying: SgmlPage = {
    if (_underlying == null || window.getEnclosedPage.getUrl != _underlying.getUrl) {
      _underlying = window.getEnclosedPage match {
        case page: SgmlPage => page
        case page: TextPage =>
          val response = new StringWebResponse(page.getContent, page.getUrl)
          HTMLParser.parseHtml(response, page.getEnclosingWindow)
      }
    }
    _underlying
  }

  override def location: String = underlying.getUrl.toString

  override def root: Element = HtmlUnitElement(underlying.getDocumentElement)
}
