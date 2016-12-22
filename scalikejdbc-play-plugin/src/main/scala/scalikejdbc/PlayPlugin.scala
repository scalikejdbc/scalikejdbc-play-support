/*
 * Copyright 2012 Kazuhiro Sera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package scalikejdbc

import play.api._
import scalikejdbc.config.{ TypesafeConfig, TypesafeConfigReader, DBs }

/**
 * The Play plugin to use ScalikeJDBC
 */
class PlayPlugin(implicit app: Application) extends Plugin {

  import PlayPlugin._

  // Play DB configuration

  private[this] lazy val playConfig = app.configuration.getConfig("scalikejdbc.play").getOrElse(Configuration.empty)

  private[this] var closeAllOnStop = true

  /**
   * DBs with Play application configuration.
   */
  private[this] lazy val DBs = new DBs with TypesafeConfigReader with TypesafeConfig {
    override val config = app.configuration.underlying
  }

  private[this] lazy val DBsWithEnv = (envValue: String) => {
    new DBs with TypesafeConfigReader with TypesafeConfig {
      override val config = app.configuration.underlying
      override val env = Option(envValue)
    }
  }

  override def onStart(): Unit = {
    Option(System.getProperty("play.db.env")) match {
      case Some("") | None => DBs.setupAll()
      case Some(env) => DBsWithEnv(env).setupAll()
    }
    opt("closeAllOnStop", "enabled")(playConfig).foreach { enabled => closeAllOnStop = enabled.toBoolean }
  }

  override def onStop(): Unit = {
    if (closeAllOnStop) {
      ConnectionPool.closeAll()
    }
  }
}

object PlayPlugin {
  def opt(name: String, key: String)(implicit config: Configuration): Option[String] = {
    config.getString(name + "." + key)
  }
}

