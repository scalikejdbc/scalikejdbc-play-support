/*
 * Copyright 2013 - 2014 scalikejdbc.org
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
import scala.concurrent.Future

/**
 *  Play Module
 */
class PlayFixtureModule extends Module {
  def bindings(env: Environment, config: Configuration) = Seq(
    bind[PlayInitializer].toSelf.eagerly,
    bind[PlayFixture].toSelf.eagerly
  )
}

/**
 * The Play fixture plugin
 */
@Singleton
class PlayFixture @Inject() (
  configuration: Configuration,
  environment: Environment,
  playInitializer: PlayInitializer,
  lifecycle: ApplicationLifecycle)
    extends scalikejdbc.play.FixtureSupport {

  private def isTest = environment.mode == Mode.Test

  private def isDev = environment.mode == Mode.Dev

  def onStart(): Unit = {
    if (isTest || isDev) {
      loadFixtures()(environment, configuration)
    }
  }

  def onStop(): Unit = {
    if (isTest || isDev) {
      cleanFixtures()(environment, configuration)
    }
  }

  lifecycle.addStopHook(() => Future.successful(onStop))
  onStart()
}
