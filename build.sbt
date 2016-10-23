name := "scrawler"

val scalaV = "2.11.8"
val crossScalaV = Seq("2.11.8", "2.12.0-RC2")

// ---- kind projector for nicer type lambdas ----
val kindProjectorPlugin = Seq(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.2")
)

// ---- library for nicer typeclasses
val simulacrumPlugin = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"
)

// ---- formatting ----

scalaVersion in ThisBuild := scalaV
scalafmtConfig in ThisBuild := Some(file(".scalafmt"))

// ---- ammonite ----

val ammonite = Seq(
  libraryDependencies += "com.lihaoyi" % "ammonite" % "0.7.8" % "test" cross CrossVersion.full,
  initialCommands in (Test, console) := """ammonite.Main().run()"""
)

ammonite // enable in root project

// ---- enable wartremover ----

import wartremover.Wart._
val wartRemover = Seq(
  wartremoverWarnings in (Compile, compile) := Seq(
    Any,
    Any2StringAdd,
    AsInstanceOf,
    DefaultArguments,
    EitherProjectionPartial,
    Enumeration,
    Equals,
    ExplicitImplicitTypes,
    FinalCaseClass,
    ImplicitConversion,
    IsInstanceOf,
    JavaConversions,
    LeakingSealed,
    ListOps,
    MutableDataStructures,
    NoNeedForMonad,
    NonUnitStatements,
    Nothing,
    Null,
    Option2Iterable,
    OptionPartial,
    Overloading,
    Product,
    Return,
    Serializable,
    ToString,
    TryPartial,
    Var,
    While
  )
)

// ---- common settings ----

val commonSettings = Seq(
    organization := "com.kadekm",
    scalaVersion := scalaV,
    scalacOptions := Seq(
      // following two lines must be "together"
      "-encoding",
      "UTF-8",
      "-Xlint",
      "-Xlint:missing-interpolator",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Ywarn-dead-code",
      "-Yno-adapted-args",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-Ywarn-value-discard",
      "-Ywarn-unused-import",
      "-Ywarn-unused",
      "-Ywarn-numeric-widen"
    )
  ) ++ ammonite ++ wartRemover ++ kindProjectorPlugin ++ simulacrumPlugin

// ---- publising ----

val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

val publishSettings = Seq()

// ---- modules ----

lazy val scraper = Project(id = "scraper", base = file("modules/scraper")).settings(
  commonSettings,
  libraryDependencies ++= Seq(
    "co.fs2"                   %% "fs2-core" % "0.9.1",
    "org.jsoup"                % "jsoup"     % "1.10.1",
    "net.sourceforge.htmlunit" % "htmlunit"  % "2.23",
    //"org.typelevel" %% "cats" % "0.7.2",
    "org.scalatest" %% "scalatest" % "3.0.0" % Test
  )
)

lazy val scrawler = Project(id = "scrawler", base = file("modules/scrawler"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.0" % Test
    )
  )
  .dependsOn(scraper)
  .aggregate(scraper)
