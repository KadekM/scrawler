package com.marekkadek.scrawler.crawlers

import com.marekkadek.scraper._
import fs2._
import fs2.util._
import fs2.util.syntax._

import scala.util.Random

sealed trait Yield[+A]
final case class YieldData[A](a: A) extends Yield[A]
final case class Visit(url: String) extends Yield[Nothing]

trait SequentialCrawlingCapability[F[_], A] {
  def sequentialCrawl(url: String): Stream[F, A]
}

trait ParallelCrawlingCapability[F[_], A] {
  def parallelCrawl(url: String, maxConnections: Int)(implicit AI: Async[F]): Stream[F, A]
}

abstract class Crawler[F[_], A](browsers: Seq[Browser[F]])(implicit FI: Effect[F])
    extends SequentialCrawlingCapability[F, A]
    with ParallelCrawlingCapability[F, A] {

  protected def onDocument(document: Document): Stream[F, Yield[A]]

  protected def pickBrowser(forUrl: String): F[Browser[F]] = FI.delay[Browser[F]] {
    val r = new Random()
    browsers(r.nextInt(browsers.size))
  }

  override def sequentialCrawl(url: String): Stream[F, A] =
    Stream
      .unfoldEval[F, List[Yield[A]], Option[A]](List[Yield[A]](Visit(url))) {

        case (u @ Visit(url)) :: rest =>
          // run the stream, executing it's effects in process
          for {
            browser  <- pickBrowser(url)
            document <- browser.fromUrl(url)
            result   <- onDocument(document).runLog.map(xs => Some((None, rest ::: xs.toList)))
          } yield result

        case (u @ YieldData(data)) :: rest =>
          FI.pure {
            Some((Some(data), rest))
          }

        case _ => FI.pure(None)
      }
      .collect {
        case Some(a) => a
      }

  override def parallelCrawl(url: String, maxConnections: Int)(implicit AI: Async[F]): Stream[F, A] =
    Stream
      .unfoldEval[F, List[Yield[A]], Option[Seq[A]]](List[Yield[A]](Visit(url))) { xs =>
        if (xs.isEmpty) FI.pure(None) // if there is nothing, stop the computation!
        else {
          // Split all of our actions to yielding data and visiting websites
          val yields: List[YieldData[A]] = xs.collect { case x: YieldData[A] => x }
          val visits: List[Visit]        = xs.collect { case x: Visit        => x }

          // If there is nothing to yield, run all visitings in parallel
          if (yields.isEmpty) {
            concurrent
              .join(maxConnections) {
                val streams = visits.map(v =>
                  Stream.eval {
                    for {
                      browser  <- pickBrowser(v.url)
                      document <- browser.fromUrl(v.url)
                    } yield document
                })
                Stream.emits(streams)
              }
              .runLog
              .flatMap { docs =>
                docs
                  .map(onDocument) // stream of work
                  //evaluate effects -> each yields sequence of work in effect F,
                  // so to avoid Seq[F[Seq[_]] we traverse to F[Seq[Seq[_]]
                  // and flatten
                  .parallelTraverse(_.runLog)
                  .map(_.flatten)
                  .map { next =>
                    // don't yield anything (since we're just visiting all pages), and set next step to be yielded stuff
                    // - we don't need to concatenate, since `yields` were empty and we have just executed all `visits`
                    Some((Option.empty[Seq[A]], next.toList))
                  }
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
        case None     => Stream.empty
      }

}
