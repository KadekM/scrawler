package com.kadekm.scrawler.crawlers

import com.kadekm.scraper._
import fs2._
import fs2.util._
import fs2.util.syntax._

sealed trait Yield[+A]
final case class YieldData[A](a: A) extends Yield[A]
final case class Visit(url: String) extends Yield[Nothing]

// TODO api would probably be better if there was single crawler with 2 methods (parallel, single)
// and taking url/maxOpenCon etc. as arguments

abstract class Crawler[F[_]: Async, A] {

  def stream: Stream[F, A]

}

abstract class SequentialCrawler[F[_], A](browser: Browser[F])(implicit FI: Async[F]) extends Crawler[F, A] {

  def url: String

  protected def onDocument(document: Document): Stream[Pure, Yield[A]]

  final def stream: Stream[F, A] =
    Stream.unfoldEval[F, List[Yield[A]], Option[A]](List[Yield[A]](Visit(url))) {
      case (u@Visit(url)) :: rest =>
        browser.fromUrl(url).map(onDocument).map(_.toList).map { xs =>
          Some((None, rest ::: xs))
        }

      case (u@YieldData(data)) :: rest => FI.pure {
        Some((Some(data), rest))
      }

      case _ => FI.pure(None)
    }
    .collect {
      case Some(a) => a
    }

}

abstract class ParallelCrawler[F[_], A](browser: Browser[F])(implicit FI: Async[F]) extends Crawler[F, A] {

  def url: String

  def maxOpenConnections: Int

  protected def onDocument(document: Document): Stream[Pure, Yield[A]]

  final def stream: Stream[F, A] =
    Stream.unfoldEval[F, List[Yield[A]], Option[Seq[A]]](List[Yield[A]](Visit(url))) { xs =>
      if (xs.isEmpty) FI.pure(None) // if there is nothing, stop the computation!
      else {
        // Split all of our actions to yielding data and visiting websites
        val yields: List[YieldData[A]] = xs.collect { case x: YieldData[A] => x }
        val visits: List[Visit] = xs.collect { case x: Visit => x }

        // If there is nothing to yield, run all visitings in parallel
        if (yields.isEmpty) {
          concurrent.join(maxOpenConnections) {
            val streams = visits.map(v => Stream.eval(browser.fromUrl(v.url)))
            Stream.emits(streams)
          }.runLog.map { docs =>
            val next = docs.map(onDocument).flatMap(_.toList)

            // don't yield anything (since we're just visiting all pages), and set next step to be yielded stuff
            // - we don't need to concatenate, since `yields` were empty and we have just executed all `visits`
            Some((Option.empty[Seq[A]], next.toList))
          }
        } else {
          // If there is something to yield, yield it all
          FI.pure {
            Some((Some(yields.map(_.a)), visits))
          }
        }
      }
    }
      .flatMap {
        case Some(xs) => Stream.emits(xs)
        case None => Stream.empty
      }

}
