addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.4")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
