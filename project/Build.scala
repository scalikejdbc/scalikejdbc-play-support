import sbt._
import Keys._

object ScalikeJDBCPlaySupportProjects extends Build {

  lazy val scalikejdbcVersion = "2.0.0"
  lazy val _version = "2.3.1-SNAPSHOT"

  // published dependency version
  lazy val _defaultPlayVersion = play.core.PlayVersion.current

  // internal only
  lazy val _h2Version = "1.4.178"

  lazy val commonSettings = Seq(
    scalaVersion := "2.10.4",
    crossScalaVersions := scalaVersion.value :: "2.11.1" :: Nil,
    scalacOptions ++= _scalacOptions
  )

  lazy val baseSettings = commonSettings ++ Seq(
    organization := "org.scalikejdbc",
    version := _version,
    publishTo <<= version { (v: String) => _publishTo(v) },
    publishMavenStyle := true,
    resolvers ++= _resolvers,
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
    // NOTE
    // Name hashing is disabled because the following error occurred when running
    // zentask test application for play-plugin or play-dbplugin-adapter.
    //
    // > project play-plugin-test-zentasks
    // [play-plugin-test-zentasks] $ run
    //
    // UnsupportedOperationException: The `++` operation is not supported for relations with different values of `nameHashing` flag.
    //
    // incOptions := incOptions.value.withNameHashing(true),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    pomExtra := _pomExtra
  )

  // scalikejdbc-play-plugin
  lazy val scalikejdbcPlayPlugin = Project(
    id = "play-plugin",
    base = file("scalikejdbc-play-plugin")
  ).settings(
    baseSettings ++ Seq(
      name := "scalikejdbc-play-plugin",
      libraryDependencies ++= Seq(
        "org.scalikejdbc"   %% "scalikejdbc"               % scalikejdbcVersion  % "compile",
        "org.scalikejdbc"   %% "scalikejdbc-config"        % scalikejdbcVersion  % "compile",
        "com.typesafe.play" %%  "play"                     % _defaultPlayVersion % "provided",
        // play-jdbc is needed to test with DBPlugin
        "com.typesafe.play" %% "play-jdbc"                 % _defaultPlayVersion % "test",
        "com.typesafe.play" %% "play-test"                 % _defaultPlayVersion % "test",
        "com.h2database"    %  "h2"                        % _h2Version          % "test"
      )
    ) : _*
  )

  // scalikejdbc-play-dbplugin-adapter
  lazy val scalikejdbcPlayDBPluginAdapter = Project(
    id = "play-dbplugin-adapter",
    base = file("scalikejdbc-play-dbplugin-adapter")
  ).settings(
    baseSettings ++ Seq(
      name := "scalikejdbc-play-dbplugin-adapter",
      libraryDependencies ++= Seq(
        "org.scalikejdbc"   %% "scalikejdbc"               % scalikejdbcVersion  % "compile",
        "org.scalikejdbc"   %% "scalikejdbc-config"        % scalikejdbcVersion  % "compile",
        "com.typesafe.play" %% "play"                      % _defaultPlayVersion % "provided",
        "com.typesafe.play" %% "play-jdbc"                 % _defaultPlayVersion % "compile",
        "com.typesafe.play" %% "play-test"                 % _defaultPlayVersion % "test",
        "com.h2database"    %  "h2"                        % _h2Version          % "test"
      )
    ) : _*
  )

  // scalikejdbc-play-fixture-plugin
  lazy val scalikejdbcPlayFixturePlugin = Project(
    id = "play-fixture-plugin",
    base = file("scalikejdbc-play-fixture-plugin")
  ).settings(
    baseSettings ++ Seq(
      name := "scalikejdbc-play-fixture-plugin",
      libraryDependencies ++= Seq(
        "org.scalikejdbc"   %% "scalikejdbc"               % scalikejdbcVersion  % "compile",
        "com.typesafe.play" %% "play"                      % _defaultPlayVersion % "provided",
        "com.typesafe.play" %% "play-test"                 % _defaultPlayVersion % "test",
        "com.h2database"    %  "h2"                        % _h2Version          % "test"
      ),
      testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "sequential", "true")
    ) : _*
  ).dependsOn(scalikejdbcPlayPlugin % "test->test")

  // play plugin zentasks example
  lazy val scalikejdbcPlayPluginTestZentasks = {
    val appName         = "play-plugin-test-zentasks"

    val appDependencies = Seq(
      "org.scalikejdbc"      %% "scalikejdbc"               % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-interpolation" % scalikejdbcVersion,
      // TODO play-flyway release version
      "com.github.tototoshi" %% "play-flyway" % "1.0.5-SNAPSHOT",
      "com.h2database"       %  "h2"          % _h2Version,
      "org.postgresql"       %  "postgresql"  % "9.3-1101-jdbc41"
    )

    Project(appName, file("scalikejdbc-play-plugin/test/zentasks"))
    .enablePlugins(play.PlayScala)
    .settings(commonSettings :_*)
    .settings(
      libraryDependencies ++= appDependencies,
      resolvers += Opts.resolver.sonatypeSnapshots, // play-flyway
      resolvers += "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
    ).dependsOn(scalikejdbcPlayPlugin, scalikejdbcPlayFixturePlugin)
  }

  // play dbplugin adapter zentasks example
  lazy val scalikejdbcPlayDBPluginAdapterTestZentasks = {
    val appName         = "play-dbplugin-adapter-test-zentasks"

    val appDependencies = Seq(
      "org.scalikejdbc"      %% "scalikejdbc"               % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-interpolation" % scalikejdbcVersion,
      "com.h2database"       %  "h2"          % _h2Version,
      "org.postgresql"       %  "postgresql"  % "9.3-1100-jdbc41"
    )

    Project(appName, file("scalikejdbc-play-dbplugin-adapter/test/zentasks"))
    .enablePlugins(play.PlayScala)
    .settings(commonSettings :_*)
    .settings(
      libraryDependencies ++= appDependencies,
      resolvers += "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
    ).dependsOn(scalikejdbcPlayDBPluginAdapter, scalikejdbcPlayFixturePlugin)
  }

  def _publishTo(v: String) = {
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
  val _resolvers = Seq(
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases",
    "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases"
  )
  val jdbcDriverDependenciesInTestScope = Seq(
    "com.h2database"    % "h2"                   % _h2Version        % "test",
    "org.apache.derby"  % "derby"                % "10.10.2.0"       % "test",
    "org.xerial"        % "sqlite-jdbc"          % "3.7.2"           % "test",
    "org.hsqldb"        % "hsqldb"               % "2.3.2"           % "test",
    "mysql"             % "mysql-connector-java" % "5.1.30"          % "test",
    "org.postgresql"    % "postgresql"           % "9.3-1101-jdbc41" % "test"
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

