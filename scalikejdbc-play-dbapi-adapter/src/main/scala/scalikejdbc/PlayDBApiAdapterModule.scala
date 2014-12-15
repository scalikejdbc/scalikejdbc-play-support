/*
 * Copyright 2014 scalikejdbc.org
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

import javax.inject._
import play.api._
import play.api.inject._
import play.api.db.{ DBApi, DBApiProvider, BoneConnectionPool }
import scalikejdbc.config.{ TypesafeConfig, TypesafeConfigReader, DBs }

/**
 * Play module
 */
class PlayDBApiAdapterModule extends Module {
  def bindings(env: Environment, config: Configuration) = Seq(
    bind[PlayDBApiAdapter].toSelf.eagerly
  )
}

/**
 * The Play plugin to use ScalikeJDBC
 */
@Singleton
class PlayDBApiAdapter @Inject() (
    dbApi: DBApi,
    configuration: Configuration,
    lifecycle: ApplicationLifecycle) {

  /**
   * DBs with Play application configuration.
   */
  private[this] lazy val DBs = new DBs with TypesafeConfigReader with TypesafeConfig {
    override val config = configuration.underlying
  }

  def onStart(): Unit = {
    DBs.loadGlobalSettings()

    dbApi.databases.foreach { db =>
      scalikejdbc.ConnectionPool.add(Symbol(db.name), new DataSourceConnectionPool(db.dataSource))
    }

    configuration.getString("scalikejdbc.play.closeAllOnStop.enabled").foreach { _ =>
      Logger.warn(s"closeAllOnStop is ignored by PlayDBPluginAdapter")
    }
  }

  onStart()
}
