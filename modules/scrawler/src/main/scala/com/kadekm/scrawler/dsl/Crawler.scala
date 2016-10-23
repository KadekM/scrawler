package com.kadekm.scrawler.dsl

import com.kadekm.scraper._
import fs2._
import fs2.util._
import fs2.util.syntax._

sealed trait Yield[+A]
final case class YieldData[A](a: A) extends Yield[A]
final case class Visit(url: String) extends Yield[Nothing]

abstract class Crawler[F[_], A](browser: Browser[F])(implicit FI: Async[F]) {

  def url: String

  //def maxOpenConnections: Int

  protected def onDocument(document: Document): Stream[Pure, Yield[A]]

  final def stream: Stream[F, A] =
    Stream.unfoldEval[F, List[Yield[A]], Yield[A]](List[Yield[A]](Visit(url))) {
      case (u@Visit(url)) :: rest =>
        browser.fromUrl(url).map(onDocument).map(_.toList).map { xs =>
          Some((u, rest ::: xs))
        }

      case (u@YieldData(data)) :: rest => FI.pure {
        Some((u, rest))
      }

      case _ => FI.pure(None)
    }
    .collect {
      // we are only interested in data that we had yielded
      case YieldData(a) => a
    }

}
