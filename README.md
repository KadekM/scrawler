# S(cala) Crawler

[![Build Status](https://travis-ci.org/KadekM/scrawler.svg?branch=dev)](https://travis-ci.org/KadekM/scrawler)
[![Maven Central](https://img.shields.io/maven-central/v/com.marekkadek/scrawler_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com.marekkadek/scrawler_2.11)

# Install

```scala
libraryDependencies += "com.marekkadek" %% "scrawler" % "0.0.2"
```

Library cross compiles for Scala 2.11 and 2.12.

# Usage

## Crawlers

You can create your specific crawler by subclassing `Crawler` class. Lets see how would it look,
for a crawler who's effects (crawling web) are captured by `fs2.Task` and that gives us data only in
form of `String`. Let's make a crawler that follows every https link and gives us url's of websites.

```scala
class MyCrawler extends Crawler[Task, String](Seq(JsoupBrowser[Task])) {
  override protected def onDocument(document: Document): Stream[Task, Yield[String]] = {
      val title = YieldData(document.location)
      val followableLinks = document.root
        .select("a[href^='https://']")  // follow only links starting by https
        .toSeq
        .flatMap(_.attr("href")) // get the href attribute from link
        .map(Visit) // visit those links

      // first yield title of website as data, and then continue by visiting links
      Stream.emit(title) ++ Stream.emits(followableLinks)
  }
}
```

We are streaming actions such as `YieldData` and `Visit`, which are currently only two allowed. Here's how Yield is defined:

```scala
sealed trait Yield[+A]
final case class YieldData[A](a: A) extends Yield[A]
final case class Visit(url: String) extends Yield[Nothing]
```
We can execute either sequential or parallel crawling.

```scala
val crawler = new MyCrawler

// crawl wikipedia sequentially and take 10 elements (titles of visited websites)
val titles: Vector[String] = crawler.sequentialCrawl("https://wikipedia.org")
    .take(10).runLog.unsafeRun

// crawl wikipedia in parallel and take 10 elements(titles of visited websites)
implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(128)
val titles2: Vector[String] = crawler.parallelCrawl("https://wikipedia.org", maxConnections = 8)
    .take(10).runLog.unsafeRun
```

You might as well pipe them into file or kafka or anything that is happy with fs2 :)

As observed in example when extending Crawler, it takes sequence of browsers to use during crawling.
By default, it randomly selects which browser to use. You can change this behaviour by overriding pickBrowser method.

```scala
class MyCrawler extends Crawler[Task, String](Seq(JsoupBrowser[Task])) {
  override protected def onDocument(document: Document): Stream[Task, Yield[String]] = ???

  // picking browser may be effectful
  override protected def pickBrowser(forUrl: String): Task[Browser[Task]] = ???
 }
```

## Browsers

Any browser that implements `Browser` trait can be used. Currently, there is JsoupBrowser, and HtmlUnit (work in progress).

To create JsoupBrowser, you can use `JsoupBrowser[Task]` (or different effect if you're not using Task).
It has several overloads, i.e. you can also pass in proxy (or user agent or so):

```scala
val proxy = ProxySettings.http("122.193.14.106", 81)
val browser = JsoupBrowser[Task](proxy)
val browser2 = JsoupBrowser[Task](connectionTimeout = 5.seconds,
    userAgent = "Mozilla",
    validateTLSCertificates = false)

```



# Credits

Greatly inspired by awesome [https://github.com/ruippeixotog/scala-scraper](Rui's scala-scraper) and python's Scrapy. Thank you!
