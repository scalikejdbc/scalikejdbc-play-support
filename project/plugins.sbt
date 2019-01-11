resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("org.scalariform"   % "sbt-scalariform" % "1.8.2")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.7.0-RC9")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.3.4")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "1.1.2")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
