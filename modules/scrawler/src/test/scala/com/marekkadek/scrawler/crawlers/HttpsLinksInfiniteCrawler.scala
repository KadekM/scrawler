package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.{Browser, Document}
import fs2._

final class HttpsLinksInfiniteCrawler(browser: Browser[Task])(implicit strategy: Strategy) extends Crawler[Task, String](browser) {
  override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
    val title = YieldData(document.location)
    val followableLinks = document.root
      .select("a[href^='https://']") // only links with href
      .map(_.attr("href"))
      .toSeq
      .flatMap(_.toSeq)
      .map(Visit)

    Stream.emit(title) ++ Stream.emits(followableLinks)
  }
}
