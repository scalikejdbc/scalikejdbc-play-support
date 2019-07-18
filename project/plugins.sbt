resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("org.scalariform"   % "sbt-scalariform" % "1.8.3")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.7.3")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.4.2")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "1.1.2")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
