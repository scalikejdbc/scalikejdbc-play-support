/*
 * Copyright 2012 - 2014 scalikejdbc.org
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
import _root_.play.api._
import _root_.play.api.inject._
import scalikejdbc.config.{ TypesafeConfig, TypesafeConfigReader, DBs }
import scala.concurrent.Future

/**
 * The Play plugin to use ScalikeJDBC
 */
@Singleton
class PlayInitializer @Inject() (
  lifecycle: ApplicationLifecycle,
  configuration: Configuration
) {

  import PlayInitializer._

  // Play DB configuration

  private[this] lazy val playConfig = configuration
    .getOptional[Configuration]("scalikejdbc.play")
    .getOrElse(Configuration.empty)

  private[this] var closeAllOnStop = true

  private[this] lazy val loggingSQLErrors = configuration
    .getOptional[Boolean]("scalikejdbc.global.loggingSQLErrors")
    .getOrElse(true)

  /**
   * DBs with Play application configuration.
   */
  private[this] lazy val DBs = new DBs
    with TypesafeConfigReader
    with TypesafeConfig {
    override val config = configuration.underlying
  }

  def onStart(): Unit = {
    DBs.setupAll()
    GlobalSettings.loggingSQLErrors = loggingSQLErrors
    opt("closeAllOnStop", "enabled")(playConfig).foreach { enabled =>
      closeAllOnStop = enabled.toBoolean
    }
  }

  def onStop(): Unit = {
    if (closeAllOnStop) {
      ConnectionPool.closeAll()
    }

    val cache = SQLSyntaxSupportFeature.SQLSyntaxSupportLoadedColumns
    cache.clear()
  }

  lifecycle.addStopHook(() => Future.successful(onStop()))
  onStart()
}

object PlayInitializer {
  def opt(name: String, key: String)(implicit
    config: Configuration
  ): Option[String] = {
    config.getOptional[String](name + "." + key)
  }
}
