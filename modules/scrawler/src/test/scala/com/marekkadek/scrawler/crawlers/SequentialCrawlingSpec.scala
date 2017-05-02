package com.marekkadek.scrawler.crawlers

import com.marekkadek.scrawler.{ScrawlerTest, opencrawling}
import fs2._

trait SequentialCrawlingSpec extends ScrawlerTest with BrowserAgnostic[Task] {
  // todo: this should not be needed, remove async from sequential
  implicit val s: Strategy = Strategy.fromFixedDaemonPool(maxThreads = 1, "test")

  val bot: SequentialCrawlingCapability[Task, String] = new HttpsLinksInfiniteCrawler(browsers)

  "sequential crawling" - {
    "printing" ignore {
      bot
        .sequentialCrawl(opencrawling.randomUrl.unsafeRun)
        .evalMap(x => Task.delay { info(s"crawled: $x"); x })
        .take(5)
        .run
        .unsafeRun
    }
  }

}
