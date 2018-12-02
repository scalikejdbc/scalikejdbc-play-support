resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("org.scalariform"   % "sbt-scalariform" % "1.8.2")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.7.0-RC8")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.3.3")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "1.1.0")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
