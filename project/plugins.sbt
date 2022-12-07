addSbtPlugin("org.scalariform"   % "sbt-scalariform" % "1.8.3")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.8.18")
addSbtPlugin("com.github.sbt"    % "sbt-pgp"         % "2.2.1")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "always"
