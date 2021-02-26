val excludeTestsIfWindows = Set(
  // TODO
  // new line char?
  "scalikejdbc.play.FixtureSpec",
)

ThisBuild / Test / testOptions ++= {
  if (scala.util.Properties.isWin) {
    Seq(Tests.Exclude(excludeTestsIfWindows))
  } else {
    Nil
  }
}
