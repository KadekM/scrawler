package com.marekkadek.scraper.htmlunit

import com.marekkadek.scraper.Browser
import fs2.util._

// Abstract until implemented
abstract sealed class HtmlUnitBrowser[F[_]](implicit FI: Effect[F]) extends Browser[F] {}
