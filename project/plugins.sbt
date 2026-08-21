addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.11")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
