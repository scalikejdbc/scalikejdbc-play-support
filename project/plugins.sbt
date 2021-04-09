addSbtPlugin("org.scalariform"   % "sbt-scalariform" % "1.8.3")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.8.8")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.5.2")
addSbtPlugin("com.github.sbt"    % "sbt-pgp"         % "2.1.2")
addSbtPlugin("com.typesafe.sbt"  % "sbt-twirl"       % "1.5.1") // https://github.com/sbt/sbt/issues/6400

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
