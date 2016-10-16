name := "scrawler"

val scalaV = "2.11.8"

// ---- kind projector for nicer type lambdas ----
val kindProjectorPlugin = Seq(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.0")
)

// ---- library for nicer typeclasses
val simulacrumPlugin = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.9.0"
)

// ---- formatting ----

scalafmtConfig in ThisBuild := Some(file(".scalafmt"))

// ---- enable wartremover ----

import wartremover.Wart._
wartremoverWarnings in (Compile, compile) := Seq(
  // Any, // needs to ignore BuildInfo
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
  //ListOps, // not ready
  //MutableDataStructures, // disagree, sometimes they are more clear
  //NoNeedForMonad, // compiler dies! (v1.1.0)
  //NonUnitStatements, // too non-scalaomatic
  //Nothing, // too many nothings in scala
  Null,
  Option2Iterable,
  OptionPartial,
  //Overloading // problem with too many overloaded external APIs
  //Product, // bugged (v1.1.0)
  Return,
  //Serializable, // bugged (v.1.1.0)
  ToString,
  TryPartial,
  Var,  // remove if performance is critical
  While // remove if performance is critical
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
) ++ kindProjectorPlugin ++ simulacrumPlugin

libraryDependencies += "com.lihaoyi" % "ammonite" % "0.7.8" % "test" cross CrossVersion.full
initialCommands in (Test, console) := """ammonite.Main().run()"""

// ---- publising ----

val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

val publishSettings = Seq()

// ---- modules ----

lazy val scraper = Project(id = "scraper", base = file("modules/scraper"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.9.2",
      "org.typelevel" %% "cats" % "0.7.2"
    )
  )

lazy val scrawler = Project(id = "scrawler", base = file("modules/scrawler"))
    .settings(
      commonSettings,
      libraryDependencies ++= Seq(
        "co.fs2" %% "fs2-core" % "0.9.1"
      )
    )



