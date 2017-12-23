package scalikejdbc.play

import org.specs2.mutable._
import org.specs2.specification.BeforeAfterEach
import play.api.{ Configuration, Environment }

import scala.collection.JavaConverters._

class FixtureSupportSpec extends Specification with BeforeAfterEach {

  def before = {
  }

  def after = {
  }

  val fixtureSupport = new FixtureSupport {}

  "FixtureSupport" should {

    "has #fixtures" in {
      val environment = Environment.simple()
      val configuration = Configuration(
        "play.modules.enabled" -> List(
          "scalikejdbc.PlayModule",
          "scalikejdbc.PlayFixtureModule"),
        "db.default.fixtures.test" -> List("users.sql", "project.sql").asJava,
        "db.secondary.fixtures.test" -> "a.sql",
        "db.default.driver" -> "org.h2.Driver",
        "db.default.url" -> "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1",
        "db.default.user" -> "sa",
        "db.default.password" -> "sa",
        "db.secondary.driver" -> "org.h2.Driver",
        "db.secondary.url" -> "jdbc:h2:mem:secondary;DB_CLOSE_DELAY=-1",
        "db.secondary.user" -> "l",
        "db.secondary.password" -> "g")
      fixtureSupport.fixtures(environment, configuration) must have size 2
    }

  }

}
