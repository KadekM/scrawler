package com.marekkadek.scraper.htmlunit

import java.net.URL
import java.util.UUID

import com.gargoylesoftware.htmlunit._
import com.marekkadek.scraper.proxy._
import com.marekkadek.scraper._
import fs2.util._

// todo: very(!!!) experimental
sealed class HtmlUnitBrowser[F[_]] private (val proxySettings: Option[HttpProxy],
                                            browserVersion: BrowserVersion)(implicit FI: Effect[F])
    extends Browser[F] {

  override def fromUrl(url: String): F[Document] = FI.delay {
    val client = proxySettings match {
      case Some(proxy) => new WebClient(browserVersion, proxy.host, proxy.port)
      case None        => new WebClient(browserVersion)
    }

    val window = client.openTargetWindow(client.getCurrentWindow, null, UUID.randomUUID().toString)

    val urll    = new URL(url)
    val request = new WebRequest(urll, HttpMethod.GET)
    request.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml")
    request.setAdditionalHeader("Accept-Charset", "utf-8")

    client.getPage(window, request)
    HtmlUnitDocument(window)
  }
}

object HtmlUnitBrowser {
  def apply[F[_]: Effect](browserVersion: BrowserVersion): HtmlUnitBrowser[F] =
    new HtmlUnitBrowser[F](None, browserVersion)
  def apply[F[_]: Effect](browserVersion: BrowserVersion, proxy: HttpProxy): HtmlUnitBrowser[F] =
    new HtmlUnitBrowser[F](Some(proxy), browserVersion)
}
