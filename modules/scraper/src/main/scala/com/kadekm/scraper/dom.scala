package com.kadekm.scraper

/**
  * Represents HTML Document
  */
trait Document {
  def location: String

  def root: Element
}

trait Element {
  def tagName: String

  def text: String

  def children: Iterable[Element]
}
