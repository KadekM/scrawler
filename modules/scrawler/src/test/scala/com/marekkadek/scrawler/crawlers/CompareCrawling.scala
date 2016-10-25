package com.marekkadek.scrawler.crawlers

import com.marekkadek.scrawler.ScrawlerTest
import org.scalatest.Matchers._
import com.marekkadek.scrawler.opencrawling._
import fs2._

trait CompareCrawling extends ScrawlerTest with BrowserAgnostic[Task] {

  implicit val s: Strategy = Strategy.fromFixedDaemonPool(maxThreads = 8, "test")

  "comparision between sequential and parallel crawling" - {

    "parallel should be faster" in {
      val crawler = new HttpsLinksInfiniteCrawler(browser)
      val url = randomUrl.unsafeRun

      val amount = 5L

      val t1  = System.nanoTime
      crawler.sequentialCrawl(url).take(amount).run.unsafeRun
      val t1e = System.nanoTime
      val d1 = t1e-t1

      val t2  = System.nanoTime
      crawler.parallelCrawl(url, maxConnections = 8).take(amount).run.unsafeRun
      val t2e = System.nanoTime
      val d2 = t2e-t2

      info(s"crawling $url")
      info(s"sequential crawl time: ${d1/1000000.0}s")
      info(s"parallel crawl time: ${d2/1000000.0}s")
      d2 should be < d1
    }
  }
}
