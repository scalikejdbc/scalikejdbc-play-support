resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("org.scalariform"   % "sbt-scalariform" % "1.8.3")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.8.5")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.5.1")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "2.0.1")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
