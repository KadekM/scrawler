package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.Browser
import com.marekkadek.scrawler.ScrawlerTest
import fs2.Strategy
import fs2.util._
import fs2._

trait ParallelCrawlingSpec extends ScrawlerTest with BrowserAgnostic[Task] {

  implicit val s: Strategy = Strategy.fromFixedDaemonPool(maxThreads = 64, "test")

  val bot: ParallelCrawlingCapability[Task, String] = new HttpsLinksInfiniteCrawler(browser)

  "something" - {
    "somewhere" ignore {
      bot
        .parallelCrawl("http://www.github.com", maxConnections = 8)
        .evalMap(x => Task.delay { println(x); x })
        .take(5)
        .run
        .unsafeRun
    }
  }
}
