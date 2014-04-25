/*
 * Copyright 2014 Takashi Kawachi
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
import play.api.db.DBPlugin
import scalikejdbc.config.{ TypesafeConfig, TypesafeConfigReader, DBs }

/**
 * The Play plugin to use ScalikeJDBC
 */
class PlayDBPluginAdapter(implicit app: Application) extends Plugin {

  private[this] lazy val dbApi = app.plugin[DBPlugin].map(_.api)
    .getOrElse(sys.error("there should be a database plugin registered at this point but looks like it's not available. Please make sure you register a db plugin properly"))

  /**
   * DBs with Play application configuration.
   */
  private[this] lazy val DBs = new DBs with TypesafeConfigReader with TypesafeConfig {
    override val config = app.configuration.underlying
  }

  override def onStart(): Unit = {
    DBs.loadGlobalSettings()

    dbApi.datasources.foreach {
      case (dataSource, name) =>
        ConnectionPool.add(Symbol(name), new DataSourceConnectionPool(dataSource))
    }

    app.configuration.getString("scalikejdbc.play.closeAllOnStop.enabled").foreach { _ =>
      Logger.warn(s"closeAllOnStop is ignored by PlayDBPluginAdapter")
    }
  }
}

