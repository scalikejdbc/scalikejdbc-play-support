lazy val scalikejdbcVersion = "4.3.5"

// published dependency version
lazy val defaultPlayVersion = play.core.PlayVersion.current

// internal only
lazy val h2Version = "2.4.240"
lazy val postgresqlVersion = "42.7.8"

lazy val commonSettings = Seq(
  scalaVersion := "2.13.17",
  crossScalaVersions := Seq("2.13.17", "3.3.6"),
  libraryDependencySchemes += "org.scala-lang.modules" %% "scala-parser-combinators" % "always",
  Test / fork := true,
  javaOptions ++= {
    if (scala.util.Properties.isWin) {
      Seq("-Dfile.encoding=Windows-31J")
    } else {
      Nil
    }
  },
  scalacOptions ++= Seq("-deprecation", "-unchecked")
)

commonSettings

lazy val baseSettings = commonSettings ++ Seq(
  organization := "org.scalikejdbc",
  version := "3.0.1-scalikejdbc-4.3",
  publishTo := (
    if (isSnapshot.value)
      None
    else
      Some(Opts.resolver.sonatypeStaging)
  ),
  publishMavenStyle := true,
  libraryDependencies += "org.specs2" %% "specs2-core" % "4.22.0" % "test",
  Global / transitiveClassifiers := Seq(Artifact.SourceClassifier),
  Test / publishArtifact := false,
  pomIncludeRepository := { x => false },
  pomExtra := _pomExtra
)

// scalikejdbc-play-initializer
lazy val scalikejdbcPlayInitializer = Project(
  id = "play-initializer",
  base = file("scalikejdbc-play-initializer")
).settings(
  baseSettings,
  name := "scalikejdbc-play-initializer",
  libraryDependencies ++= Seq(
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion % "provided",
    "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion % "provided",
    "org.playframework" %% "play" % defaultPlayVersion % "provided",
    // play-jdbc is needed to test with DBApi
    "org.playframework" %% "play-jdbc" % defaultPlayVersion % "test",
    "org.playframework" %% "play-test" % defaultPlayVersion % "test",
    "com.h2database" % "h2" % h2Version % "test",
    guice % "test"
  ),
)

// scalikejdbc-play-dbapi-adapter
lazy val scalikejdbcPlayDBApiAdapter = Project(
  id = "play-dbapi-adapter",
  base = file("scalikejdbc-play-dbapi-adapter")
).settings(
  baseSettings,
  name := "scalikejdbc-play-dbapi-adapter",
  libraryDependencies ++= Seq(
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion % "provided",
    "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion % "provided",
    "org.playframework" %% "play" % defaultPlayVersion % "provided",
    "org.playframework" %% "play-jdbc" % defaultPlayVersion % "compile",
    "org.playframework" %% "play-test" % defaultPlayVersion % "test",
    "com.h2database" % "h2" % h2Version % "test",
    guice % "test"
  ),
)

// scalikejdbc-play-fixture
lazy val scalikejdbcPlayFixture = Project(
  id = "play-fixture",
  base = file("scalikejdbc-play-fixture")
).settings(
  baseSettings,
  name := "scalikejdbc-play-fixture",
  libraryDependencies ++= Seq(
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion % "provided",
    "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion % "provided",
    "org.playframework" %% "play" % defaultPlayVersion % "provided",
    "org.playframework" %% "play-test" % defaultPlayVersion % "test",
    "com.h2database" % "h2" % h2Version % "test"
  ),
  Test / testOptions += Tests
    .Argument(TestFrameworks.Specs2, "sequential", "true"),
).dependsOn(scalikejdbcPlayInitializer)

// play plugin zentasks example
lazy val scalikejdbcPlayInitializerTestZentasks = {
  val appName = "play-initializer-test-zentasks"

  val appDependencies = Seq(
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
    "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
    "org.flywaydb" %% "flyway-play" % "9.1.0",
    "com.h2database" % "h2" % h2Version,
    "org.postgresql" % "postgresql" % postgresqlVersion
  )

  Project(appName, file("scalikejdbc-play-initializer/test/zentasks"))
    .enablePlugins(play.sbt.PlayScala, PlayNettyServer)
    .disablePlugins(PlayPekkoHttpServer)
    .settings(
      commonSettings,
      libraryDependencies ++= appDependencies,
    )
    .dependsOn(scalikejdbcPlayInitializer, scalikejdbcPlayFixture)
}

// play dbapi adapter zentasks example
lazy val scalikejdbcPlayDBApiAdapterTestZentasks = {
  val appName = "play-dbapi-adapter-test-zentasks"

  val appDependencies = Seq(
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
    "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
    "com.h2database" % "h2" % h2Version,
    "org.postgresql" % "postgresql" % postgresqlVersion
  )

  Project(appName, file("scalikejdbc-play-dbapi-adapter/test/zentasks"))
    .enablePlugins(play.sbt.PlayScala, PlayNettyServer)
    .disablePlugins(PlayPekkoHttpServer)
    .settings(
      commonSettings,
      libraryDependencies ++= appDependencies,
    )
    .dependsOn(scalikejdbcPlayDBApiAdapter, scalikejdbcPlayFixture)
}

val jdbcDriverDependenciesInTestScope = Seq(
  "com.h2database" % "h2" % h2Version % "test",
  "org.apache.derby" % "derby" % "10.10.2.+" % "test",
  "org.xerial" % "sqlite-jdbc" % "3.7.+" % "test",
  "org.hsqldb" % "hsqldb" % "2.3.+" % "test",
  "com.mysql" % "mysql-connector-j" % "8.2.0" % "test",
  "org.postgresql" % "postgresql" % postgresqlVersion % "test"
)
val _pomExtra = <url>https://scalikejdbc.org/</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>https://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:scalikejdbc/scalikejdbc-play-support.git</url>
      <connection>scm:git:git@github.com:scalikejdbc/scalikejdbc-play-support.git</connection>
    </scm>
    <developers>
      <developer>
        <id>seratch</id>
        <name>Kazuhiro Sera</name>
        <url>https://github.com/seratch</url>
      </developer>
      <developer>
        <id>xuwei-k</id>
        <name>Kenji Yoshida</name>
        <url>https://github.com/xuwei-k</url>
      </developer>
      <developer>
        <id>tkawachi</id>
        <name>Takashi Kawachi</name>
        <url>https://github.com/tkawachi</url>
      </developer>
      <developer>
        <id>tototoshi</id>
        <name>Toshiyuki Takahashi</name>
        <url>https://github.com/tototoshi</url>
      </developer>
    </developers>
