package com.marekkadek.scraper.jsoup

import com.marekkadek.scraper.{Browser, Document}
import com.marekkadek.scraper.proxy.ProxySettings
import fs2.util._
import org.jsoup.{Connection, Jsoup}

sealed class JsoupBrowser[F[_]] private (val proxySettings: Option[ProxySettings] = Option.empty)(
    implicit FI: Effect[F])
    extends Browser[F] {
  override def fromUrl(url: String): F[Document] =
    FI.delay {
      //println(s"${Thread.currentThread.getName} > $url") // todo: remove :)

      val con = Jsoup.connect(url)

      con.followRedirects(true)
      proxySettings.foreach(x => con.proxy(x.proxy))

      // todo (#19) execute() may throw exceptions
      val r = con.execute()

      JsoupDocument(r.parse)
    }
}

object JsoupBrowser {
  def apply[F[_]: Effect]                       = new JsoupBrowser[F]
  def apply[F[_]: Effect](proxy: ProxySettings) = new JsoupBrowser[F](Some(proxy))

  def readInputStream[F[_]](is: java.io.InputStream, charset: java.nio.charset.Charset, baseUri: String)(
      implicit FI: Effect[F]): F[Document] = FI.delay {
    val doc = Jsoup.parse(is, charset.displayName, baseUri)
    JsoupDocument(doc)
  }
}
