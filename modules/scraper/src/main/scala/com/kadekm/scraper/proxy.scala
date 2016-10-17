package com.kadekm.scraper

import fs2.util.Suspendable

import fs2.util.syntax.FunctorOps
import fs2.util.syntax.MonadOps

final case class ProxyTuple(host: String, port: Int)

final case class ProxySettings(httpProxy: Option[ProxyTuple],
                               httpsProxy: Option[ProxyTuple],
                               socksProxy: Option[ProxyTuple]) {
  def applyToSystem[F[_]](implicit FI: Suspendable[F]): F[Unit] = for {
    _ <- httpProxy.fold(FI.pure[Unit](())) (x => proxy.setHttpProxy(x.host, x.port))
    _ <- httpsProxy.fold(FI.pure[Unit](())) (x => proxy.setHttpsProxy(x.host, x.port))
    _ <- socksProxy.fold(FI.pure[Unit](())) (x => proxy.setHttpsProxy(x.host, x.port))
  } yield ()
}

private[scraper] object proxy {

  private[this] val HTTP_PROXY_HOST: String = "http.proxyHost"
  private[this] val HTTP_PROXY_PORT: String = "http.proxyPort"
  private[this] val HTTPS_PROXY_PORT: String = "https.proxyPort"
  private[this] val HTTPS_PROXY_HOST: String = "https.proxyHost"
  private[this] val SOCKS_PROXY_HOST: String = "socksProxyHost"
  private[this] val SOCKS_PROXY_PORT: String = "socksProxyPort"

  private[this] type FF[F[_]] = Suspendable[F]

  /**
    * Sets the JVM-wide HTTP proxy configuration.
    * @param host the proxy host
    * @param port the proxy port
    */
  def setHttpProxy[F[_]](host: String, port: Int)(implicit FI: FF[F]): F[Unit] = FI.delay {
    System.setProperty(HTTP_PROXY_HOST, host)
    System.setProperty(HTTP_PROXY_PORT, String.valueOf(port))
  }

  /**
    * Sets the JVM-wide HTTP proxy configuration.
    * @param host the proxy host
    * @param port the proxy port
    */
  def setHttpsProxy[F[_]](host: String, port: Int)(implicit FI: FF[F]): F[Unit] = FI.delay {
    System.setProperty(HTTPS_PROXY_HOST, host)
    System.setProperty(HTTPS_PROXY_PORT, String.valueOf(port))
  }

  /**
    * Returns the current JVM-wide HTTP proxy configuration.
    * @return the current JVM-wide HTTP proxy configuration.
    */
  def getHttpProxy[F[_]](implicit FI: FF[F]): F[Option[ProxyTuple]] = FI.delay {
    for {
      host <- Option(System.getProperty(HTTP_PROXY_HOST))
      port <- Option(System.getProperty(HTTP_PROXY_PORT))
    } yield ProxyTuple(host, port.toInt)
  }

  /**
    * Returns the current JVM-wide HTTPS proxy configuration.
    * @return the current JVM-wide HTTPS proxy configuration.
    */
  def getHttpsProxy[F[_]](implicit FI: FF[F]): F[Option[ProxyTuple]] = FI.delay {
    for {
      host <- Option(System.getProperty(HTTPS_PROXY_HOST))
      port <- Option(System.getProperty(HTTPS_PROXY_PORT))
    } yield ProxyTuple(host, port.toInt)
  }

  /**
    * Unsets the JVM-wide HTTP proxy configuration.
    */
  def removeHttpProxy[F[_]](implicit FI: FF[F]): F[Unit] = FI.delay {
    System.clearProperty(HTTP_PROXY_HOST)
    System.clearProperty(HTTP_PROXY_PORT)
  }

  /**
    * Unsets the JVM-wide HTTPS proxy configuration.
    */
  def removeHttpsProxy[F[_]](implicit FI: FF[F]): F[Unit] = FI.delay {
    System.clearProperty(HTTPS_PROXY_HOST)
    System.clearProperty(HTTPS_PROXY_PORT)
  }

  /**
    * Sets the JVM-wide SOCKS proxy configuration.
    * @param host the proxy host
    * @param port the proxy port
    */
  def setSocksProxy[F[_]](host: String, port: Int)(implicit FI: FF[F]): F[Unit] = FI.delay {
    System.setProperty(SOCKS_PROXY_HOST, host)
    System.setProperty(SOCKS_PROXY_PORT, String.valueOf(port))
  }

  /**
    * Returns the current JVM-wide SOCKS proxy configuration.
    * @return the current JVM-wide SOCKS proxy configuration.
    */
  def getSocksProxy[F[_]](implicit FI: FF[F]): F[Option[(String, Int)]] = FI.delay {
    for {
      host <- Option(System.getProperty(SOCKS_PROXY_HOST))
      port <- Option(System.getProperty(SOCKS_PROXY_PORT))
    } yield (host, port.toInt)
  }

  /**
    * Unsets the JVM-wide SOCKS proxy configuration.
    */
  def removeSocksProxy[F[_]](implicit FI: FF[F]): F[Unit] = FI.delay {
    System.clearProperty(SOCKS_PROXY_HOST)
    System.clearProperty(SOCKS_PROXY_PORT)
  }
}
