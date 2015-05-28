import sbt._
import Keys._

object ScalikeJDBCPlaySupportProjects extends Build {

  lazy val scalikejdbcVersion = "2.2.6"
  lazy val _version = "2.4.0.RC1"

  // published dependency version
  lazy val defaultPlayVersion = play.core.PlayVersion.current

  // internal only
  lazy val h2Version = "1.4.187"
  lazy val postgresqlVersion = "9.4-1201-jdbc41"

  lazy val commonSettings = Seq(
    scalaVersion := "2.11.6",
    crossScalaVersions := Seq("2.11.6", "2.10.5"),
    scalacOptions ++= _scalacOptions
  )

  lazy val baseSettings = commonSettings ++ Seq(
    organization := "org.scalikejdbc",
    version := _version,
    publishTo <<= version { (v: String) => _publishTo(v) },
    publishMavenStyle := true,
    resolvers ++= _resolvers,
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.6" % "test",
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    pomExtra := _pomExtra
  )

  // scalikejdbc-play-initializer
  lazy val scalikejdbcPlayInitializer = Project(
    id = "play-initializer",
    base = file("scalikejdbc-play-initializer")
  ).settings(
    baseSettings ++ Seq(
      name := "scalikejdbc-play-initializer",
      libraryDependencies ++= Seq(
        "org.scalikejdbc"   %% "scalikejdbc"        % scalikejdbcVersion  % "provided",
        "org.scalikejdbc"   %% "scalikejdbc-config" % scalikejdbcVersion  % "provided",
        "com.typesafe.play" %% "play"               % defaultPlayVersion  % "provided",
        // play-jdbc is needed to test with DBApi
        "com.typesafe.play" %% "play-jdbc"          % defaultPlayVersion  % "test",
        "com.typesafe.play" %% "play-test"          % defaultPlayVersion  % "test",
        "com.h2database"    %  "h2"                 % h2Version           % "test"
      )
    ) : _*
  )

  // scalikejdbc-play-dbapi-adapter
  lazy val scalikejdbcPlayDBApiAdapter = Project(
    id = "play-dbapi-adapter",
    base = file("scalikejdbc-play-dbapi-adapter")
  ).settings(
    baseSettings ++ Seq(
      name := "scalikejdbc-play-dbapi-adapter",
      libraryDependencies ++= Seq(
        "org.scalikejdbc"   %% "scalikejdbc"        % scalikejdbcVersion  % "provided",
        "org.scalikejdbc"   %% "scalikejdbc-config" % scalikejdbcVersion  % "provided",
        "com.typesafe.play" %% "play"               % defaultPlayVersion  % "provided",
        "com.typesafe.play" %% "play-jdbc"          % defaultPlayVersion  % "compile",
        "com.typesafe.play" %% "play-test"          % defaultPlayVersion  % "test",
        "com.h2database"    %  "h2"                 % h2Version           % "test"
      )
    ) : _*
  )

  // scalikejdbc-play-fixture
  lazy val scalikejdbcPlayFixture = Project(
    id = "play-fixture",
    base = file("scalikejdbc-play-fixture")
  ).settings(
    baseSettings ++ Seq(
      name := "scalikejdbc-play-fixture",
      libraryDependencies ++= Seq(
        "org.scalikejdbc"   %% "scalikejdbc"        % scalikejdbcVersion  % "provided",
        "com.typesafe.play" %% "play"               % defaultPlayVersion  % "provided",
        "com.typesafe.play" %% "play-test"          % defaultPlayVersion  % "test",
        "org.scalikejdbc"   %% "scalikejdbc-config" % scalikejdbcVersion  % "test",
        "com.h2database"    %  "h2"                 % h2Version           % "test"
      ),
      testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "sequential", "true")
    ) : _*
  ).dependsOn(scalikejdbcPlayInitializer % "test->test")

  // play plugin zentasks example
  lazy val scalikejdbcPlayInitializerTestZentasks = {
    val appName         = "play-initializer-test-zentasks"

    val appDependencies = Seq(
      "org.scalikejdbc"      %% "scalikejdbc" % scalikejdbcVersion,
      "com.github.tototoshi" %% "play-flyway" % "1.2.+",
      "com.h2database"       %  "h2"          % h2Version,
      "org.postgresql"       %  "postgresql"  % postgresqlVersion
    )

    Project(appName, file("scalikejdbc-play-initializer/test/zentasks"))
    .enablePlugins(play.sbt.Play)
    .settings(commonSettings :_*)
    .settings(
      libraryDependencies ++= appDependencies,
      resolvers += "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
    ).dependsOn(scalikejdbcPlayInitializer, scalikejdbcPlayFixture)
  }

  // play dbapi adapter zentasks example
  lazy val scalikejdbcPlayDBApiAdapterTestZentasks = {
    val appName         = "play-dbapi-adapter-test-zentasks"

    val appDependencies = Seq(
      "org.scalikejdbc"      %% "scalikejdbc" % scalikejdbcVersion,
      "com.h2database"       %  "h2"          % h2Version,
      "org.postgresql"       %  "postgresql"  % postgresqlVersion
    )

    Project(appName, file("scalikejdbc-play-dbapi-adapter/test/zentasks"))
    .enablePlugins(play.sbt.Play)
    .settings(commonSettings :_*)
    .settings(
      libraryDependencies ++= appDependencies,
      resolvers += "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
    ).dependsOn(scalikejdbcPlayDBApiAdapter, scalikejdbcPlayFixture)
  }

  def _publishTo(v: String) = {
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
  val _resolvers = Seq(
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases",
    "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
  )
  val jdbcDriverDependenciesInTestScope = Seq(
    "com.h2database"    % "h2"                   % h2Version         % "test",
    "org.apache.derby"  % "derby"                % "10.10.2.+"       % "test",
    "org.xerial"        % "sqlite-jdbc"          % "3.7.+"           % "test",
    "org.hsqldb"        % "hsqldb"               % "2.3.+"           % "test",
    "mysql"             % "mysql-connector-java" % "5.1.+"           % "test",
    "org.postgresql"    % "postgresql"           % postgresqlVersion % "test"
  )
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
          <name>Kazuhiro Sera</name>
          <url>http://git.io/sera</url>
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
      </developers>

}
