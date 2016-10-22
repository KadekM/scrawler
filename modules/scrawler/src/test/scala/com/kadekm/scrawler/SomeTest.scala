package com.kadekm.scrawler

import com.kadekm.scraper._
import com.kadekm.scraper.jsoup.JsoupBrowser
import com.kadekm.scrawler.dsl._
import com.kadekm.scraper.extractors._

import fs2.{Strategy, Task}

class SomeTest extends ScrawlerTest {

  // todo: proper tests

  "something" - {
    "foo" in {
      implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(8, "foo")
      val browser = JsoupBrowser[Task]

      val wikiCrawler = new Crawler[Task, String](browser) {
        override val url = "https://en.wikipedia.org/wiki/Main_Page"

        // such links that begins with https and have `external` class
        val allAtags = Css("a[href^='https://'].external")

        val titleSelect = Css("head title")

        override def onElement(document: Document) = {
          case el @ allAtags(atags) if document.location.contains("wikipedia.org") =>
            ParallelVisitUrls(atags.map(_.attr("href").get).toSeq)

          case other @ titleSelect(title) =>
            YieldData(title.head.text)
        }

      }

      val result = wikiCrawler.stream.runLog.unsafeRun
      println(result)
      println("done")
    }
  }
}

