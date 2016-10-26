package com.marekkadek.scrawler

import com.marekkadek.scraper.Document
import com.marekkadek.scraper.jsoup.JsoupBrowser
import com.marekkadek.scrawler.crawlers._
import fs2._

class SomeTest extends ScrawlerTest {

  // todo: proper tests

  implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(128, "foo")
  val browser                     = JsoupBrowser[Task]

  "something" - {
    "sequential crawler" ignore {
      val url = "https://blog.scrapinghub.com"

      val wikiCrawler = new Crawler[Task, String](browser) {
        override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
          val entries = for (title <- document.root.select("h2.entry-title"))
            yield YieldData(s"${document.location} - ${title.text}")

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

      val result = wikiCrawler.sequentialCrawl(url).runLog.unsafeRun
      println(result.mkString("\n\n"))
      println("done")
    }

    "sequential crawler on wiki links" ignore {
      val url = "https://en.wikipedia.org/wiki/Main_Page"

      val wikiCrawler = new Crawler[Task, String](browser) {
        override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
          val title = for (title <- document.root.select("head > title"))
            yield YieldData(s"${document.location} - ${title.text}")

          val externalLinks =
            document.root.select("a[href^='https://']").map(_.attr("href").get).map(Visit).toSeq

          Stream.emits(title.toSeq) ++ Stream.emits(externalLinks)
        }
      }

      val result = wikiCrawler.sequentialCrawl(url).take(20).runLog.unsafeRun
      println(result.mkString("\n\n"))
      println("done")
    }

    "parallel crawler on wiki links" ignore {
      val url = "https://en.wikipedia.org/wiki/Main_Page"
      val maxOpenConnections: Int = 30

      val wikiCrawler = new Crawler[Task, String](browser) {
        override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
          val title = for (title <- document.root.select("head > title"))
            yield YieldData(s"${document.location} - ${title.text}")

          val externalLinks =
            document.root.select("a[href^='https://']").map(_.attr("href").get).map(Visit).toSeq

          Stream.emits(title.toSeq) ++ Stream.emits(externalLinks)
        }
      }

      val result = wikiCrawler
        .parallelCrawl(url, maxOpenConnections)
        .take(100)
        .evalMap(x => Task.delay { println(s"${Thread.currentThread} $x");x })
        .run.unsafeRun
      println("done")
    }
  }
}
