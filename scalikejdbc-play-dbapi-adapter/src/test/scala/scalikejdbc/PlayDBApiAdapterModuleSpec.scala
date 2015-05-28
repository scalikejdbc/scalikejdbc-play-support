package scalikejdbc

import scalikejdbc._

import org.specs2.mutable.Specification

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play.current

object PlayDBPluginAdapterSpec extends Specification {

  sequential

  Class.forName("org.h2.Driver")

  def fakeAppWithDBPlugin = FakeApplication(
    withoutPlugins = Seq("play.api.cache.EhCachePlugin"),
    additionalConfiguration = Map(
      "play.modules.enabled" -> List(
        "scalikejdbc.PlayDBApiAdapterModule",
        "play.api.db.DBModule",
        "play.api.db.BoneCPModule",
        "play.api.inject.BuiltinModule"
      ),
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:default",
      "db.default.user" -> "sa",
      "db.default.password" -> "sa",
      "db.default.schema" -> "",
      "db.legacydb.driver" -> "org.h2.Driver",
      "db.legacydb.url" -> "jdbc:h2:mem:legacy",
      "db.legacydb.user" -> "l",
      "db.legacydb.password" -> "g",
      "db.legacydb.schema" -> "",
      "scalikejdbc.global.loggingSQLAndTime.enabled" -> "true",
      "scalikejdbc.global.loggingSQLAndTime.logLevel" -> "debug",
      "scalikejdbc.global.loggingSQLAndTime.warningEnabled" -> "true",
      "scalikejdbc.global.loggingSQLAndTime.warningThreasholdMillis" -> "1",
      "scalikejdbc.global.loggingSQLAndTime.warningLogLevel" -> "warn"
    )
  )

  def fakeAppWithoutDBPlugin = FakeApplication(
    withoutPlugins = Seq("play.api.cache.EhCachePlugin"),
    additionalConfiguration = Map(
      "play.modules.enabled" -> List("scalikejdbc.PlayDBApiAdapterModule"),
      "play.modules.disabled" -> List(
        "play.api.db.DBModule",
        "play.api.db.BoneCPModule",
        "play.api.inject.BuiltinModule"
      ),
      "logger.root" -> "INFO",
      "logger.play" -> "INFO",
      "logger.application" -> "DEBUG",
      "evolutionplugin" -> "disabled",
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:default",
      "db.default.user" -> "sa",
      "db.default.password" -> "sa",
      "db.default.schema" -> "",
      "db.default.poolInitialSize" -> "1",
      "db.default.poolMaxSize" -> "2",
      "db.default.poolValidationQuery" -> "select 1",
      "db.default.poolConnectionTimeoutMillis" -> "2000",
      "db.legacydb.driver" -> "org.h2.Driver",
      "db.legacydb.url" -> "jdbc:h2:mem:legacy",
      "db.legacydb.user" -> "l",
      "db.legacydb.password" -> "g",
      "db.legacydb.schema" -> "",
      "scalikejdbc.global.loggingSQLAndTime.enabled" -> "true",
      "scalikejdbc.global.loggingSQLAndTime.singleLineMode" -> "true",
      "scalikejdbc.global.loggingSQLAndTime.logLevel" -> "debug",
      "scalikejdbc.global.loggingSQLAndTime.warningEnabled" -> "true",
      "scalikejdbc.global.loggingSQLAndTime.warningThreasholdMillis" -> "1",
      "scalikejdbc.global.loggingSQLAndTime.warningLogLevel" -> "warn"
    )
  )

  def simpleTest(table: String) = {

    try {
      DB autoCommit { implicit s =>
        SQL("DROP TABLE " + table + " IF EXISTS").execute.apply()
        SQL("CREATE TABLE " + table + " (ID BIGINT PRIMARY KEY NOT NULL, NAME VARCHAR(256))").execute.apply()
        val insert = SQL("INSERT INTO " + table + " (ID, NAME) VALUES (/*'id*/123, /*'name*/'Alice')")
        insert.bindByName('id -> 1, 'name -> "Alice").update.apply()
        insert.bindByName('id -> 2, 'name -> "Bob").update.apply()
        insert.bindByName('id -> 3, 'name -> "Eve").update.apply()
      }

      NamedDB('legacydb) autoCommit { implicit s =>
        SQL("DROP TABLE " + table + " IF EXISTS").execute.apply()
        SQL("CREATE TABLE " + table + " (ID BIGINT PRIMARY KEY NOT NULL, NAME VARCHAR(256))").execute.apply()
        val insert = SQL("INSERT INTO " + table + " (ID, NAME) VALUES (/*'id*/123, /*'name*/'Alice')")
        insert.bindByName('id -> 1, 'name -> "Alice").update.apply()
        insert.bindByName('id -> 2, 'name -> "Bob").update.apply()
        insert.bindByName('id -> 3, 'name -> "Eve").update.apply()
        insert.bindByName('id -> 4, 'name -> "Fred").update.apply()
      }

      case class User(id: Long, name: Option[String])

      val users = DB readOnly { implicit s =>
        SQL("SELECT * FROM " + table).map(rs => User(rs.long("id"), Option(rs.string("name")))).list.apply()
      }
      users.size must_== (3)

      val usersInLegacy = NamedDB('legacydb) readOnly { implicit s =>
        SQL("SELECT * FROM " + table).map(rs => User(rs.long("id"), Option(rs.string("name")))).list.apply()
      }
      usersInLegacy.size must_== (4)

    } finally {
      DB autoCommit { implicit s =>
        SQL("DROP TABLE " + table + " IF EXISTS").execute.apply()
      }
      NamedDB('legacydb) autoCommit { implicit s =>
        SQL("DROP TABLE " + table + " IF EXISTS").execute.apply()
      }
    }
  }

  "PlayDBPluginAdapter" should {

    "be available when DB plugin is also active" in {
      running(fakeAppWithDBPlugin) { simpleTest("user_withdbplugin") }
    }

    "not be available when DB plugin is not active" in {
      running(fakeAppWithoutDBPlugin) {} must throwA[RuntimeException]
    }

  }

}
