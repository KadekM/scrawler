package com.kadekm.scraper

import fs2._
import fs2.util._

abstract class Browser[F[_]: Effect] {
  def fromUrl(url: String): F[Document]
}
