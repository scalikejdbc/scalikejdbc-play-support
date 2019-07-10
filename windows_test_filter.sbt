val excludeTestsIfWindows = Set(
  // TODO
  // new line char?
  "scalikejdbc.play.FixtureSpec",
)

testOptions in Test in ThisBuild ++= {
  if (scala.util.Properties.isWin) {
    Seq(Tests.Exclude(excludeTestsIfWindows))
  } else {
    Nil
  }
}
