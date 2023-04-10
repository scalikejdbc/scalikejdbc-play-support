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
import org.slf4j.LoggerFactory
import _root_.play.api._
import _root_.play.api.inject._
import _root_.play.api.db.{ DBApi, DBApiProvider }
import scalikejdbc.config.{ TypesafeConfig, TypesafeConfigReader, DBs }
import scala.concurrent.Future

/**
 * The Play plugin to use ScalikeJDBC
 */
@Singleton
class PlayDBApiAdapter @Inject() (
  dbApi: DBApi,
  configuration: Configuration,
  lifecycle: ApplicationLifecycle
) {

  val logger = LoggerFactory.getLogger(this.getClass)

  /**
   * DBs with Play application configuration.
   */
  private[this] lazy val DBs = new DBs
    with TypesafeConfigReader
    with TypesafeConfig {
    override val config = configuration.underlying
  }

  private[this] lazy val loggingSQLErrors = configuration
    .getOptional[Boolean]("scalikejdbc.global.loggingSQLErrors")
    .getOrElse(true)

  def onStart(): Unit = {
    DBs.loadGlobalSettings()
    GlobalSettings.loggingSQLErrors = loggingSQLErrors

    dbApi.databases.foreach { db =>
      scalikejdbc.ConnectionPool.add(
        db.name,
        new DataSourceConnectionPool(db.dataSource)
      )
    }

    configuration
      .getOptional[String]("scalikejdbc.play.closeAllOnStop.enabled")
      .foreach { _ =>
        logger.warn(s"closeAllOnStop is ignored by PlayDBPluginAdapter")
      }
  }

  def onStop(): Unit = {
    val cache = SQLSyntaxSupportFeature.SQLSyntaxSupportLoadedColumns
    cache.clear()
  }

  lifecycle.addStopHook(() => Future.successful(onStop()))
  onStart()
}
