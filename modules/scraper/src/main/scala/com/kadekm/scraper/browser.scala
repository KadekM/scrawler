package com.kadekm.scraper

import fs2._
import fs2.util._

import fs2.util.syntax._

abstract class Browser[F[_]](implicit FI: Effect[F]) {
  val currentProxy: ProxySettings

  def fromUrl(url: String): F[Document]

  protected def withProxy[A](f: => F[A]): F[A] =
    for {
      before <- proxy.getProxySettings[F]
      _      <- proxy.setProxySettings(currentProxy)
      // Reset to `before` even on error
      r      <- FI.attempt(f).flatMap {
        case Right(x) => proxy.setProxySettings(before).map(_ => x)
        case Left(e) => proxy.setProxySettings(before).flatMap(_ => FI.fail[A](e))
      }
    } yield r
}
