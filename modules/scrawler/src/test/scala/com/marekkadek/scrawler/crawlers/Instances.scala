package com.marekkadek.scrawler.crawlers

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.marekkadek.scraper.Browser
import com.marekkadek.scraper.htmlunit.HtmlUnitBrowser
import com.marekkadek.scraper.jsoup.JsoupBrowser
import fs2._

class JsoupSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task](validateTLSCertificates = false))
}

/*class HtmlUnitSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(HtmlUnitBrowser[Task](BrowserVersion.CHROME))
}*/

class JsoupParallelCrawlingSpec extends ParallelCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task](validateTLSCertificates = false))
}

/*class HtmlUnitParallelCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(HtmlUnitBrowser[Task](BrowserVersion.CHROME))
}*/

class JsoupCompareCrawlingSpec extends CompareCrawling {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task](validateTLSCertificates = false))
}
