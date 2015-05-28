resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.typesafe.sbt"  % "sbt-scalariform" % "1.3.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.4.0")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.1.8")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "1.0.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype"    % "0.5.0")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
