addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.5")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.8")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
