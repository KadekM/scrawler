package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.Browser
import com.marekkadek.scraper.jsoup.JsoupBrowser
import fs2._

class JsoupSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browser: Browser[Task] = JsoupBrowser[Task]
}

/*class HtmlUnitSequentialCrawlingSpec extends SequentialCrawlingSpec {
  override def browser: Browser[Task] = ???
}*/

class JsoupParallelCrawlingSpec extends ParallelCrawlingSpec {
  override def browser: Browser[Task] = JsoupBrowser[Task]
}

/*class HtmlUnitParallelCrawlingSpec extends SequentialCrawlingSpec {
  override def browser: Browser[Task] = ???
}*/

class JsoupCompareCrawlingSpec extends CompareCrawling {
  override def browser: Browser[Task] = JsoupBrowser[Task]
}
