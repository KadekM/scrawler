package com.marekkadek.scraper

import java.nio.charset.StandardCharsets

import com.marekkadek.scraper.jsoup.JsoupBrowser
import fs2._
import fs2.Task._
import Utils._

class JsoupBrowserSpec extends ScraperTest {

  "JsoupBrowser" - {
    "reading input stream" in {
      val is = resource("/building_materials.htm").get

      val browser = JsoupBrowser.readInputStream[Task](is, StandardCharsets.UTF_8, "wikipedia.com")

      browser.unsafeRun.root.tagName shouldBe "html"
    }
  }
}
