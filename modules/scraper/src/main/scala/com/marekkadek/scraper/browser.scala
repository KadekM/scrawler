package com.marekkadek.scraper

import proxy.ProxySettings
import fs2.util._

abstract class Browser[F[_]](implicit FI: Effect[F]) {
  val proxySettings: Option[ProxySettings]

  def fromUrl(url: String): F[Document]
}
