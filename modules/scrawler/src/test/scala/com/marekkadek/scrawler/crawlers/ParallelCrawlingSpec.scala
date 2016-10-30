package com.marekkadek.scrawler.crawlers

import com.marekkadek.scrawler._
import fs2.Strategy
import fs2._

trait ParallelCrawlingSpec extends ScrawlerTest with BrowserAgnostic[Task] {

  implicit val s: Strategy = Strategy.fromFixedDaemonPool(maxThreads = 64, "test")

  val bot: ParallelCrawlingCapability[Task, String] = new HttpsLinksInfiniteCrawler(browsers)

  "parallel crawling" - {
    "printing" in {
      bot
        .parallelCrawl(opencrawling.randomUrl.unsafeRun, maxConnections = 8)
        .evalMap(x => Task.delay { info(s"crawled: $x"); x })
        .take(5)
        .run
        .unsafeRun
    }
  }
}
