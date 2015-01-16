resolvers ++= Seq("Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.7")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
