package com.marekkadek.scrawler

import fs2.Task

import scala.util.Random

object opencrawling {
  private[this] val random = new Random

  val urls: Seq[String] = Seq(
    //"https://news.ycombinator.com", //
    "http://www.github.com",
    "http://www.wikipedia.org"
    //"http://www.stackoverflow.com" //
    //"http://reddit.com" //
  )

  def randomUrl: Task[String] = Task.delay {
    urls(random.nextInt(urls.size))
  }
}
