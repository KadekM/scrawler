package com.marekkadek.scraper.jsoup

import com.marekkadek.scraper._
import com.marekkadek.scraper.proxy.ProxySettings
import fs2.util._
import org.jsoup._

import scala.concurrent.duration.Duration
import scala.concurrent.duration._

sealed class JsoupBrowser[F[_]] private (val proxySettings: Option[ProxySettings],
                                         connectionTimeout: Duration,
                                         userAgent: String,
                                         followRedirects: Boolean,
                                         validateTLSCertificates: Boolean)(implicit FI: Effect[F])
    extends Browser[F] {
  override def fromUrl(url: String): F[Document] =
    FI.delay {
      val con = Jsoup.connect(url)

      con.userAgent(userAgent)
      con.followRedirects(followRedirects)
      con.validateTLSCertificates(validateTLSCertificates)
      con.ignoreHttpErrors(true) // do not throw exceptions in `execute`
      proxySettings.foreach(x => con.proxy(x.proxy))
      con.timeout(connectionTimeout.toMillis.toInt)

      val r = con.execute()

      JsoupDocument(r.parse)
    }
}

object JsoupBrowser {
  def apply[F[_]: Effect]: JsoupBrowser[F] = JsoupBrowser[F]()
  def apply[F[_]: Effect](proxy: ProxySettings): JsoupBrowser[F] =
    JsoupBrowser[F](proxySettings = Some(proxy))
  def apply[F[_]: Effect](proxySettings: Option[ProxySettings] = Option.empty,
                          connectionTimeout: Duration = 3.seconds,
                          userAgent: String = "Mozilla",
                          followRedirects: Boolean = true,
                          validateTLSCertificates: Boolean = true): JsoupBrowser[F] =
    new JsoupBrowser[F](proxySettings,
                        connectionTimeout,
                        userAgent,
                        followRedirects,
                        validateTLSCertificates)

  def readInputStream[F[_]](is: java.io.InputStream, charset: java.nio.charset.Charset, baseUri: String)(
      implicit FI: Effect[F]): F[Document] = FI.delay {
    val doc = Jsoup.parse(is, charset.displayName, baseUri)
    JsoupDocument(doc)
  }
}
