package com.marekkadek.scraper

import java.net._

object proxy {
  sealed trait ProxySettings {
    def toProxy: Proxy
  }

  final case class HttpProxy(host: String, port: Int) extends ProxySettings {
    override def toProxy: Proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port))
  }

  final case class SocksProxy(address: InetSocketAddress) extends ProxySettings {
    override def toProxy: Proxy = new Proxy(Proxy.Type.SOCKS, address)
  }

  case object NoProxy extends ProxySettings {
    override def toProxy: Proxy = Proxy.NO_PROXY
  }
}
