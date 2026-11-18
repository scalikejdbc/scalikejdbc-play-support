addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.12")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.4.0")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
