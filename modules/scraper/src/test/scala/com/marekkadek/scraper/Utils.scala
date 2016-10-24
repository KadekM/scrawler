package com.marekkadek.scraper

import java.io.InputStream

object Utils {
  def resource(path: String): Option[InputStream] =
    Option(getClass.getResourceAsStream(path))

}
