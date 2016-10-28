package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper.Browser

trait BrowserAgnostic[F[_]] {

  def browsers: Seq[Browser[F]]

}
