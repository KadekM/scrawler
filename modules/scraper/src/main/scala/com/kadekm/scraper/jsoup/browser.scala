package com.kadekm.scraper.jsoup

import java.io.InputStream
import java.nio.charset.Charset

import com.kadekm.scraper._
import fs2._
import fs2.util._
import org.jsoup.Jsoup

sealed class JsoupBrowser[F[_]] private(proxy: ProxySettings = ProxySettings(None, None, None))
                                       (implicit FI: Effect[F]) extends Browser[F] {
  override def fromUrl(url: String): F[Document] = FI.delay {
    val doc = Jsoup.connect(url).get()
    JsoupDocument(doc)
  }
}

object JsoupBrowser {
  def apply[F[_]: Effect] = new JsoupBrowser[F]
  def apply[F[_]: Effect](proxy: ProxySettings) = new JsoupBrowser[F](proxy)

  def readInputStream[F[_]](is: java.io.InputStream, charset: java.nio.charset.Charset, baseUri: String)(
      implicit FI: Effect[F]): F[Document] = FI.delay {
    val doc = Jsoup.parse(is, charset.displayName, baseUri)
    JsoupDocument(doc)
  }
}
