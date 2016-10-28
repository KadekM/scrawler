package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper._
import com.marekkadek.scraper.jsoup.JsoupBrowser
import com.marekkadek.scraper.proxy.ProxySettings
import com.marekkadek.scrawler.ScrawlerTest
import fs2._

sealed class WhatsMyIpChecker(browsers: Seq[Browser[Task]])(implicit S: Strategy)
    extends Crawler[Task, String](browsers) {
  override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
    document.root.select("span#ip").headOption match {
      case Some(x) => Stream.emit(YieldData(x.text))
      case None    => Stream.empty
    }
  }
}

// Spurious test (if proxy goes down!)
// todo: ideally we would download all proxy list, try out which works and use that one
// todo: also for htmlunit
class JsoupProxyPickingTest extends ScrawlerTest {

  implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(8)

  val whatsmyip = "http://www.whatsmyip.org"

  // from http://proxylist.hidemyass.com
  val proxies: Seq[ProxySettings] = Seq(
    ProxySettings.http("122.193.14.106", 81)
  )

  "proxy configuration" - {
    "should be configurable per browser" - {
      proxies match {
        case fst :: _ =>
          val browserNoProxy     = JsoupBrowser[Task]
          val browserBehindProxy = JsoupBrowser[Task](fst)

          // use two different crawlers just to be sure they pick different browsers
          val checkerNoProxy     = new WhatsMyIpChecker(Seq(browserNoProxy))
          val checkerBehindProxy = new WhatsMyIpChecker(Seq(browserBehindProxy))

          info(s"check if ip differs by querying $whatsmyip")
          "in sequential crawl" in {
            val ipNoProxy     = checkerNoProxy.sequentialCrawl(whatsmyip).take(1).runLog.unsafeRun
            val ipBehindProxy = checkerBehindProxy.sequentialCrawl(whatsmyip).take(1).runLog.unsafeRun

            ipNoProxy should not equal ipBehindProxy
          }

          "in parallel crawl" in {
            // we still use only single connection, but check if parallel crawl method does not ignore proxy settings
            val ipNoProxy     = checkerNoProxy.parallelCrawl(whatsmyip, 1).take(1).runLog.unsafeRun
            val ipBehindProxy = checkerBehindProxy.parallelCrawl(whatsmyip, 1).take(1).runLog.unsafeRun

            ipNoProxy should not equal ipBehindProxy
          }

        case _ => fail("Not enough proxies set")
      }
    }
  }
}
