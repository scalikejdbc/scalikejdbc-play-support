addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0-RC3")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
