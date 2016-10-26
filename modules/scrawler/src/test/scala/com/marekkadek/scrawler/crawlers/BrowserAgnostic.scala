package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.Browser
import fs2.Task

trait BrowserAgnostic[F[_]] {

  def browser: Browser[Task]

}
