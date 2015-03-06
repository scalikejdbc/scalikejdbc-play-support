package scalikejdbc

import scalikejdbc._

import org.specs2.mutable.Specification

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play.current

import DBActionBuilders._

object DBActionBuildersSpec extends Specification with Results {

  sequential

  Class.forName("org.h2.Driver")

  def fakeApp = FakeApplication(
    withoutPlugins = Seq("play.api.cache.EhCachePlugin"),
    additionalPlugins = Seq("scalikejdbc.PlayPlugin"),
    additionalConfiguration = Map(
      "logger.root" -> "INFO",
      "logger.play" -> "INFO",
      "logger.application" -> "DEBUG",
      "dbplugin" -> "disabled",
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

  def fakeAppWithoutCloseAllOnStop = FakeApplication(
    withoutPlugins = Seq("play.api.cache.EhCachePlugin"),
    additionalPlugins = Seq("scalikejdbc.PlayPlugin"),
    additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:default",
      "db.default.user" -> "sa",
      "db.default.password" -> "sa",
      "db.legacydb.driver" -> "org.h2.Driver",
      "db.legacydb.url" -> "jdbc:h2:mem:legacy",
      "db.legacydb.user" -> "l",
      "db.legacydb.password" -> "g",
      "scalikejdbc.play.closeAllOnStop.enabled" -> "false"
    )
  )

  def fakeAppWithDBPlugin = FakeApplication(
    withoutPlugins = Seq("play.api.cache.EhCachePlugin"),
    additionalPlugins = Seq("scalikejdbc.PlayPlugin"),
    additionalConfiguration = Map(
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

  "DBActionBuilders" should {

    "create DBAction" in {
      running(fakeApp) {
        val action = DBAction { req =>
          implicit val session = req.dbSession
          SQL("DROP TABLE user_action_1 IF EXISTS").execute.apply()
          SQL("CREATE TABLE user_action_1 (ID BIGINT PRIMARY KEY NOT NULL, NAME VARCHAR(256))").execute.apply()
          val insert = SQL("INSERT INTO user_action_1 (ID, NAME) VALUES (/*'id*/123, /*'name*/'Alice')")
          insert.bindByName('id -> 1, 'name -> "Alice").update.apply()
          insert.bindByName('id -> 2, 'name -> "Bob").update.apply()
          insert.bindByName('id -> 3, 'name -> "Eve").update.apply()
          val name = SQL("SELECT name FROM user_action_1 WHERE id = 1").map(_.string(1)).single.apply()
          Ok(name.toString)
        }
        val result = action(FakeRequest())
        contentAsString(result) must_== ("Some(Alice)")
      }
    }

    "create NamedDBAction" in {
      running(fakeApp) {
        val action = NamedDBAction('legacydb) { req =>
          implicit val session = req.dbSession
          SQL("DROP TABLE user_action_2 IF EXISTS").execute.apply()
          SQL("CREATE TABLE user_action_2 (ID BIGINT PRIMARY KEY NOT NULL, NAME VARCHAR(256))").execute.apply()
          val insert = SQL("INSERT INTO user_action_2 (ID, NAME) VALUES (/*'id*/123, /*'name*/'Alice')")
          insert.bindByName('id -> 1, 'name -> "Alice").update.apply()
          insert.bindByName('id -> 2, 'name -> "Bob").update.apply()
          insert.bindByName('id -> 3, 'name -> "Eve").update.apply()
          val name = SQL("SELECT name FROM user_action_2 WHERE id = 2").map(_.string(1)).single.apply()
          Ok(name.toString)
        }
        val result = action(FakeRequest())
        contentAsString(result) must_== ("Some(Bob)")
      }
    }

    "create DBTxAction" in {
      running(fakeApp) {
        val action = DBTxAction { req =>
          implicit val session = req.dbSession
          SQL("DROP TABLE user_action_3 IF EXISTS").execute.apply()
          SQL("CREATE TABLE user_action_3 (ID BIGINT PRIMARY KEY NOT NULL, NAME VARCHAR(256))").execute.apply()
          val insert = SQL("INSERT INTO user_action_3 (ID, NAME) VALUES (/*'id*/123, /*'name*/'Alice')")
          insert.bindByName('id -> 1, 'name -> "Alice").update.apply()
          insert.bindByName('id -> 2, 'name -> "Bob").update.apply()
          insert.bindByName('id -> 3, 'name -> "Eve").update.apply()
          val name = SQL("SELECT name FROM user_action_3 WHERE id = 1").map(_.string(1)).single.apply()
          Ok(name.toString)
        }
        val result = action(FakeRequest())
        contentAsString(result) must_== ("Some(Alice)")
      }
    }

    "create NamedDBTxAction" in {
      running(fakeApp) {
        val action = NamedDBTxAction('legacydb) { req =>
          implicit val session = req.dbSession
          SQL("DROP TABLE user_action_4 IF EXISTS").execute.apply()
          SQL("CREATE TABLE user_action_4 (ID BIGINT PRIMARY KEY NOT NULL, NAME VARCHAR(256))").execute.apply()
          val insert = SQL("INSERT INTO user_action_4 (ID, NAME) VALUES (/*'id*/123, /*'name*/'Alice')")
          insert.bindByName('id -> 1, 'name -> "Alice").update.apply()
          insert.bindByName('id -> 2, 'name -> "Bob").update.apply()
          insert.bindByName('id -> 3, 'name -> "Eve").update.apply()
          val name = SQL("SELECT name FROM user_action_4 WHERE id = 2").map(_.string(1)).single.apply()
          Ok(name.toString)
        }
        val result = action(FakeRequest())
        contentAsString(result) must_== ("Some(Bob)")
      }
    }

  }

}
