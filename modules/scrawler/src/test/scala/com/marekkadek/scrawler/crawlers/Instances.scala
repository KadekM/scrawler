package com.marekkadek.scrawler.crawlers

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.marekkadek.scraper.Browser
import com.marekkadek.scraper.htmlunit.HtmlUnitBrowser
import com.marekkadek.scraper.jsoup.JsoupBrowser
import scala.concurrent.duration._
import fs2._

class JsoupSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task](
    validateTLSCertificates = false,
    connectionTimeout = 10.seconds))
}

/*class HtmlUnitSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(HtmlUnitBrowser[Task](BrowserVersion.CHROME))
}*/

class JsoupParallelCrawlingSpec extends ParallelCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task](
    validateTLSCertificates = false,
    connectionTimeout = 10.seconds))
}

/*class HtmlUnitParallelCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(HtmlUnitBrowser[Task](BrowserVersion.CHROME))
}*/

class JsoupCompareCrawlingSpec extends CompareCrawling {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task](
    validateTLSCertificates = false,
    connectionTimeout = 10.seconds))
}
