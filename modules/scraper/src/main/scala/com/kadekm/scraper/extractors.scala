package com.kadekm.scraper

object extractors {

  final case class Css(selector: String) {
    // Non-empty list
    def unapply(e: Element): Option[Iterable[Element]] = {
      val ls = e.select(selector)
      if (ls.isEmpty) None
      else Some(ls)
    }
  }

}
