package com.kadekm.scrawler

import com.kadekm.scraper._
import com.kadekm.scraper.jsoup.JsoupBrowser
import com.kadekm.scrawler.dsl._
import com.kadekm.scraper.extractors._

import fs2._

class SomeTest extends ScrawlerTest {

  // todo: proper tests

  "something" - {
    "foo" in {
      implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(8, "foo")
      val browser = JsoupBrowser[Task]

      val wikiCrawler = new Crawler[Task, String](browser) {
        override val url = "https://blog.scrapinghub.com"

        //override def maxOpenConnections: Int = 8

        override protected def onDocument(document: Document): Stream[Pure, Yield[String]] = {
          val entries = for (title <- document.root.select("h2.entry-title"))
              yield YieldData(title.text)

         val next = for {
           el <- document.root.select("div.prev-post > a").headOption
           url <- el.attr("href")
         } yield Visit(url)

          val nextLink = next match {
            case Some(x) => Stream(x)
            case None => Stream.empty
          }

          Stream.emits(entries.toSeq) ++ nextLink
        }
      }

      val result = wikiCrawler.stream.runLog.unsafeRun
      println(result.mkString("\n\n"))
      println("done")
    }
  }
}

