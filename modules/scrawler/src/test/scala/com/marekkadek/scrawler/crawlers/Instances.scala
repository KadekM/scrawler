package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.Browser
import com.marekkadek.scraper.jsoup.JsoupBrowser
import fs2._

class JsoupSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task])
}

/*class HtmlUnitSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browser: Seq[Browser[Task]] = ???
}*/

class JsoupParallelCrawlingSpec extends ParallelCrawlingSpec {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task])
}

/*class HtmlUnitParallelCrawlingSpec extends SequentialCrawlingSpec {
  override def browser: Seq[Browser[Task]] = ???
}*/

class JsoupCompareCrawlingSpec extends CompareCrawling {
  override def browsers: Seq[Browser[Task]] = Seq(JsoupBrowser[Task])
}
