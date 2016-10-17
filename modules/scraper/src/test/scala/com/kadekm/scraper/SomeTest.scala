package com.kadekm.scraper

import java.nio.charset.StandardCharsets

import Utils._
import com.kadekm.scraper.jsoup.JsoupBrowser
import fs2._
import fs2.Task._

class SomeTest extends ScraperTest {

  "something" - {
    "foo" in {
      val is = resource("/building_materials.htm").get
      val jsBrowser = JsoupBrowser[Task]

      val browser = JsoupBrowser.readInputStream[Task](is, StandardCharsets.UTF_8, "wikipedia.com")
      println(browser.unsafeRun)

    }
  }
}

