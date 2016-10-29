package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.{Browser, Document}
import fs2._

final class HttpsLinksInfiniteCrawler(browser: Seq[Browser[Task]])
    extends Crawler[Task, String](browser) {
  override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
    val title = YieldData(document.location)
    val followableLinks = document.root
      .select("a[href^='https://']")
      .toSeq
      .flatMap(_.attr("href"))
      .map(Visit)

    Stream.emit(title) ++ Stream.emits(followableLinks)
  }

}
