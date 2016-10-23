package com.kadekm.scrawler

import com.kadekm.scraper._
import com.kadekm.scraper.jsoup.JsoupBrowser
import com.kadekm.scrawler.crawlers._

import fs2._

class SomeTest extends ScrawlerTest {

  // todo: proper tests

  implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(128, "foo")
  val browser                     = JsoupBrowser[Task]

  "something" - {
    "sequential crawler" ignore {
      val wikiCrawler = new SequentialCrawler[Task, String](browser) {
        override val url = "https://blog.scrapinghub.com"

        override protected def onDocument(document: Document): Stream[Pure, Yield[String]] = {
          val entries = for (title <- document.root.select("h2.entry-title"))
            yield YieldData(title.text)

          val next = for {
            el  <- document.root.select("div.prev-post > a").headOption
            url <- el.attr("href")
          } yield Visit(url)

          val nextLink = next match {
            case Some(x) => Stream(x)
            case None    => Stream.empty
          }

          Stream.emits(entries.toSeq) ++ nextLink
        }
      }

      val result = wikiCrawler.stream.runLog.unsafeRun
      println(result.mkString("\n\n"))
      println("done")
    }

    "sequential crawler on wiki links" ignore {
      val wikiCrawler = new SequentialCrawler[Task, String](browser) {
        override val url = "https://en.wikipedia.org/wiki/Main_Page"

        override protected def onDocument(document: Document): Stream[Pure, Yield[String]] = {
          val title = for (title <- document.root.select("head > title"))
            yield YieldData(title.text)

          val externalLinks =
            document.root.select("a[href^='https://']").map(_.attr("href").get).map(Visit).toSeq

          Stream.emits(title.toSeq) ++ Stream.emits(externalLinks)
        }
      }

      val result = wikiCrawler.stream.take(20).runLog.unsafeRun
      println(result.mkString("\n\n"))
      println("done")
    }

    "parallel crawler on wiki links" ignore {
      val wikiCrawler = new ParallelCrawler[Task, String](browser) {
        override val url = "https://en.wikipedia.org/wiki/Main_Page"

        override def maxOpenConnections: Int = 30

        override protected def onDocument(document: Document): Stream[Pure, Yield[String]] = {
          val title = for (title <- document.root.select("head > title"))
            yield YieldData(title.text)

          val externalLinks =
            document.root.select("a[href^='https://']").map(_.attr("href").get).map(Visit).toSeq

          Stream.emits(title.toSeq) ++ Stream.emits(externalLinks)
        }
      }

      val result = wikiCrawler.stream.take(20).runLog.unsafeRun
      println(result.mkString("\n\n"))
      println("done")
    }
  }
}
