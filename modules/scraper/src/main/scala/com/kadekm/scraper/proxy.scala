package com.kadekm.scraper

import java.net._

object proxy {
  sealed abstract case class ProxySettings(proxy: Proxy)

  object ProxySettings {

    def http(host: String, port: Int): ProxySettings = {
      val proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port))
      new ProxySettings(proxy) {}
    }

    def socks(address: InetSocketAddress): ProxySettings = {
      val proxy = new Proxy(Proxy.Type.SOCKS, address)
      new ProxySettings(proxy) {}
    }

    def none: ProxySettings = new ProxySettings(java.net.Proxy.NO_PROXY) {}

  }
}
