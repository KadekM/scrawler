package com.kadekm.scraper

import fs2._
import fs2.util._

import fs2.util.syntax._

abstract class Browser[F[_]: Effect] {
  val currentProxy: ProxySettings

  def fromUrl(url: String): F[Document]

  protected def withProxy[A](f: => F[A]): F[A] =
    for {
      before <- proxy.getProxySettings[F]
      _      <- proxy.setProxySettings(currentProxy)
      r      <- f
      after  <- proxy.setProxySettings(before)
    } yield r
}
