package com.marekkadek.scraper

import proxy.ProxySettings
import fs2.util._

abstract class Browser[F[_]](implicit FI: Effect[F]) {
  def fromUrl(url: String): F[Document]
}
