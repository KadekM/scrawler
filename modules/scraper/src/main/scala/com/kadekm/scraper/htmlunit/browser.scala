package com.kadekm.scraper.htmlunit

import com.kadekm.scraper._
import fs2.util._
import fs2._

// Abstract until implemented
abstract sealed class HtmlUnitBrowser[F[_]](implicit FI: Effect[F]) extends Browser[F] {}
