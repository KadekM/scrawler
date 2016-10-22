package com.kadekm.scraper

/**
  * Represents HTML Document
  */
trait Document {
  def location: String

  def root: Element
}

trait Element {
  def select(query: String): Iterable[Element]

  def tagName: String

  def text: String

  def attr(name: String): Option[String]

  def children: Iterable[Element]
}
