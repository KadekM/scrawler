package com.kadekm.scrawler.dsl

import com.kadekm.scraper._
import fs2._
import fs2.util._
import fs2.util.syntax._

// todo: tests
sealed trait Decision
final case class YieldData[A](a: A) extends Decision
final case class YieldMultipleData[A](as: Seq[A]) extends Decision
final case class VisitUrl(url: String) extends Decision
final case class VisitUrls(url: Seq[String]) extends Decision
final case class ParallelVisitUrls(url: Seq[String]) extends Decision
case object Pass extends Decision

// todo: revisit
sealed trait Result[+A]
final case class Data[A](a: A) extends Result[A]
final case class MultipleData[A](as: Seq[A]) extends Result[A]
case object NoResult extends Result[Nothing]

abstract class Crawler[F[_], A](browser: Browser[F])(implicit FI: Async[F]) {

  def url: String

  def maxOpenConnections: Int

  // todo: can we do only single operation?
  def onElement(document: Document): PartialFunction[Element, Decision]

  private def safeOnElement(document: Document): Element => Decision = onElement(document).lift.andThen(_.getOrElse(Pass))

  final def stream: Stream[F, A] =
    Stream.unfoldEval[F, List[Decision], Result[A]](List(VisitUrl(url))) {

      case VisitUrl(urll) :: rest => browser.fromUrl(urll).map { doc =>
        // todo: we probably shouldn't run it on each element but have different mechanism
        val nextDecisions = doc.root.children.map(safeOnElement(doc)).toList
        Some((NoResult, rest ++ nextDecisions))
      }

      case VisitUrls(urls) :: rest => FI.pure {
        val visits = urls.map(VisitUrl)
        Some((NoResult, rest ++ visits))
      }

      case ParallelVisitUrls(urls) :: rest =>
        concurrent.join(maxOpenConnections) {
          val par = urls.map(url => Stream.eval(browser.fromUrl(url)))
          Stream.emits(par)
        }.runLog.map { docs =>
          val nextDecisions = docs.flatMap(doc => doc.root.children.map(safeOnElement(doc))).toList
          Some((NoResult, rest ++ nextDecisions))
        }

      // todo: castings no-no-no!
      case YieldData(x) :: rest => FI.pure {
        Some((Data(x.asInstanceOf[A]), rest))
      }

      case YieldMultipleData(xs) :: rest => FI.pure {
        Some((MultipleData(xs.asInstanceOf[Seq[A]]), rest))
      }

      // todo: implement rest of ops
      case _ => FI.pure(None)
    }
    .collect {  // represent it all as nested
      case Data(x) => Seq(x.asInstanceOf[A])
      case MultipleData(xs) => xs
    }
    .flatMap(Stream.emits) // flatten it all (do we want to?)

}
