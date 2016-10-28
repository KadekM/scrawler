package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.Browser
import com.marekkadek.scrawler.ScrawlerTest

import fs2._

trait SequentialCrawlingSpec extends ScrawlerTest with BrowserAgnostic[Task] {
  // todo: this should not be needed, remove async from sequential
  implicit val s: Strategy = Strategy.fromFixedDaemonPool(maxThreads = 1, "test")

  val bot: SequentialCrawlingCapability[Task, String] = new HttpsLinksInfiniteCrawler(browser)

  "something" - {
    "somewhere" ignore {
      bot
        .sequentialCrawl("http://www.github.com")
        .evalMap(x => Task.delay { println(x); x })
        .take(5)
        .run
        .unsafeRun
    }
  }

}
