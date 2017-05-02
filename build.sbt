val scalaV      = "2.11.11"
val crossScalaV = Seq("2.11.11", "2.12.2")

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
scalafmtConfig in ThisBuild := Some(file(".scalafmt.conf"))

// ---- ammonite ----
val ammonite = Seq(
  libraryDependencies += "com.lihaoyi" % "ammonite" % "0.7.8" % "test" cross CrossVersion.full,
  initialCommands in (Test, console) := """ammonite.Main().run()"""
)

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
    //NonUnitStatements,
    //Nothing,
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
    organization := "com.marekkadek",
    scalaVersion := scalaV,
    crossScalaVersions := crossScalaV,
    releaseCrossBuild := true,
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
    // Ammonite is not yet for scala 2.12
  ) ++ wartRemover ++ kindProjectorPlugin ++ simulacrumPlugin // ++ ammonite

// ---- publising ----

val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

val publishSettings = Seq(
  homepage := Some(url("https://github.com/KadekM/scrawler")),
  organizationHomepage := Some(url("https://github.com/KadekM/scrawler")),
  licenses += ("MIT license", url("http://www.opensource.org/licenses/mit-license.php")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra :=
    <scm>
      <url>git@github.com:kadekm/scrawler.git</url>
      <connection>scm:git:git@github.com:kadekm/scrawler.git</connection>
    </scm>
      <developers>
        <developer>
          <id>kadekm</id>
          <name>Marek Kadek</name>
          <url>https://github.com/KadekM</url>
        </developer>
      </developers>
)

// ---- modules ----

lazy val scraper = Project(id = "scraper", base = file("modules/scraper")).settings(
  commonSettings,
  publishSettings,
  libraryDependencies ++= Seq(
    "co.fs2"                   %% "fs2-core" % "0.9.5",
    "org.jsoup"                % "jsoup"     % "1.10.1",
    "net.sourceforge.htmlunit" % "htmlunit"  % "2.23",
    //"org.typelevel" %% "cats" % "0.7.2",
    "org.scalatest" %% "scalatest" % "3.0.0" % Test
  )
)

lazy val scrawler = Project(id = "scrawler", base = file("modules/scrawler"))
  .settings(
    commonSettings,
    publishSettings,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.0" % Test
    )
  )
  .dependsOn(scraper)
  .aggregate(scraper)

lazy val root = Project(id = "root", base = file("."))
  .settings(
    commonSettings,
    noPublishSettings
  )
  .dependsOn(scraper, scrawler)
  .aggregate(scraper, scrawler)
