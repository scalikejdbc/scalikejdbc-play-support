import sbt._
import Keys._

import play.Project._

object ScalikeJDBCPlaySupportProjects extends Build {

  //lazy val _version = "2.0.0-SNAPSHOT"
  lazy val _version = "1.7.4"

  // published dependency version
  lazy val _slf4jApiVersion = "1.7.6"
  lazy val _defaultPlayVersion = "2.2.2"
  lazy val _typesafeConfigVersion = "1.2.0"

  // internal only
  lazy val _logbackVersion = "1.1.1"
  lazy val _h2Version = "1.3.175"
  lazy val _hibernateVersion = "4.3.1.Final"
  // TODO 2.0
  lazy val _scalatestVersion = "1.9.2"
  lazy val _specs2Scala291Version = "1.12.4"
  lazy val _specs2Scala29Version = "1.12.4.1"
  lazy val _specs2Scala210Version = "2.3.7"

  lazy val baseSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.scalikejdbc",
    version := _version,
    publishTo <<= version { (v: String) => _publishTo(v) },
    publishMavenStyle := true,
    resolvers ++= _resolvers,
    scalacOptions ++= _scalacOptions,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    pomExtra := _pomExtra
  )

  // scalikejdbc-play-plugin
  lazy val scalikejdbcPlayPlugin = Project(
    id = "play-plugin",
    base = file("scalikejdbc-play-plugin"),
    settings = baseSettings ++ Seq(
      name := "scalikejdbc-play-plugin",
      libraryDependencies <++= (scalaVersion) { scalaVersion =>
        (scalaVersion match {
          case "2.10.3" | "2.10.2" | "2.10.1" | "2.10.0" => {
            Seq(
              "org.scalikejdbc"   %% "scalikejdbc"               % _version            % "compile",
              "com.typesafe.play" %  "play_2.10"                 % _defaultPlayVersion % "provided",
              "com.typesafe.play" %  "play-test_2.10"            % _defaultPlayVersion % "test",
              "com.h2database"    %  "h2"                        % _h2Version          % "test"
            )
          }
          case _ => {
            val play20Version = "2.0.8"
            Seq(
              "play" % "play_2.9.1"      % play20Version % "provided",
              "play" % "play-test_2.9.1" % play20Version % "test"
            )
          }
        })
      }
    )
  ) 

  // scalikejdbc-play-fixture-plugin
  lazy val scalikejdbcPlayFixturePlugin = Project(
    id = "play-fixture-plugin",
    base = file("scalikejdbc-play-fixture-plugin"),
    settings = baseSettings ++ Seq(
      name := "scalikejdbc-play-fixture-plugin",
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play"      % _defaultPlayVersion % "provided",
        "com.typesafe.play" %% "play-test" % _defaultPlayVersion % "test",
        "com.h2database"    %  "h2"        % _h2Version          % "test"
      ),
      testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "sequential", "true")
    )
  ).dependsOn(scalikejdbcPlayPlugin).aggregate(scalikejdbcPlayPlugin)

  // play zentasks example
  lazy val scalikejdbcPlayPluginTestZentasks = {
    val appName         = "play-plugin-test-zentasks"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.scalikejdbc"      %% "scalikejdbc"               % _version,
      "org.scalikejdbc"      %% "scalikejdbc-interpolation" % _version,
      "com.github.tototoshi" %% "play-flyway" % "1.0.1",
      "com.h2database"       %  "h2"          % _h2Version,
      "org.postgresql"       %  "postgresql"  % "9.3-1100-jdbc41"
    )

    play.Project(appName, appVersion, appDependencies, path = file("scalikejdbc-play-plugin/test/zentasks")).settings(
      scalaVersion in ThisBuild := "2.10.3",
      resolvers ++= Seq(
        "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases",
        "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
      )
    ).dependsOn(scalikejdbcPlayFixturePlugin).aggregate(scalikejdbcPlayPlugin, scalikejdbcPlayFixturePlugin)
  }

  def _publishTo(v: String) = {
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
  val _resolvers = Seq(
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases",
    "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
    "sonatype snaphots" at "http://oss.sonatype.org/content/repositories/snapshots"
  )
  val jdbcDriverDependenciesInTestScope = Seq(
    "com.h2database"    % "h2"                   % _h2Version        % "test",
    "org.apache.derby"  % "derby"                % "10.10.1.1"       % "test",
    "org.xerial"        % "sqlite-jdbc"          % "3.7.15-M1"       % "test",
    "org.hsqldb"        % "hsqldb"               % "2.3.1"           % "test",
    "mysql"             % "mysql-connector-java" % "5.1.29"          % "test",
    "org.postgresql"    % "postgresql"           % "9.3-1100-jdbc41" % "test"
  )
  //val _scalacOptions = Seq("-deprecation", "-unchecked", "-Ymacro-debug-lite", "-Xlog-free-terms", "Yshow-trees", "-feature")
  val _scalacOptions = Seq("-deprecation", "-unchecked")
  val _pomExtra = <url>http://scalikejdbc.org/</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
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
          <name>Kazuhuiro Sera</name>
          <url>http://git.io/sera</url>
        </developer>
      </developers>
}

