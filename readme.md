# ScalikeJDBC Play Support [![Build Status](https://travis-ci.org/scalikejdbc/scalikejdbc-play-support.svg)](https://travis-ci.org/scalikejdbc/scalikejdbc-play-support)

This is a project to enable using ScalikeJDBC in Play2 apps seamlessly. If you're working with Play 2.3, see the [2.3 branch](https://github.com/scalikejdbc/scalikejdbc-play-support/tree/2.3) instead.

http://scalikejdbc.org/

### Migration Guide

Unfortunately, Play 2.4 is basically incompatible with Play plugins. Since Play 2.4, you need to switch to use Play modules instead.

#### ScalikeJDBC integration with Play 2.0 - 2.3

- scalikejdbc-play-plugin
- scalikejdbc-play-dbplugin-adapter
- scalikejdbc-play-fixture-plugin

#### ScalikeJDBC integration with Play 2.4 or higher

- scalikejdbc-play-initializer
- scalikejdbc-play-dbapi-adapter
- scalikejdbc-play-fixture

### Getting Started with Play 2.8

#### build.sbt

```scala
libraryDependencies ++= Seq(
  "com.h2database"  %  "h2"                           % "1.4.200", // your jdbc driver here
  "org.scalikejdbc" %% "scalikejdbc"                  % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-config"           % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.5"
)
```

#### conf/application.conf

```
# Database configuration
# ~~~~~
# You can declare as many datasources as you want.
# By convention, the default datasource is named `default`
db.default.driver=org.h2.Driver
db.default.url="jdbc:h2:mem:play;DB_CLOSE_DELAY=-1"
# NOTE: sclaikejdbc-config 2.2.6 doesn't support username, use 2.2.7 or higher
db.default.username=sa
db.default.password=sa

# ScalikeJDBC original configuration
#db.default.poolInitialSize=10
#db.default.poolMaxSize=10
#db.default.poolValidationQuery=

scalikejdbc.global.loggingSQLErrors=true
scalikejdbc.global.loggingSQLAndTime.enabled=true
scalikejdbc.global.loggingSQLAndTime.singleLineMode=false
scalikejdbc.global.loggingSQLAndTime.logLevel=debug
scalikejdbc.global.loggingSQLAndTime.warningEnabled=true
scalikejdbc.global.loggingSQLAndTime.warningThresholdMillis=5
scalikejdbc.global.loggingSQLAndTime.warningLogLevel=warn

play.modules.enabled += "scalikejdbc.PlayModule"
```

#### app/controllers/Application.scala

Now you can access ScalikeJDBC everywhere in your Play app!

```scala
package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scalikejdbc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val session = AutoSession

  def index = Action {
    val accounts = {
      try sql"select * from accounts".toMap.list.apply()
      catch { case e: Exception =>
        sql"create table accounts(name varchar(100) not null)".execute.apply()
        Seq("Alice", "Bob", "Chris").foreach { name =>
          sql"insert into accounts values ($name)".update.apply()
        }
        sql"select * from accounts".toMap.list.apply()
      }
    }
    Ok(accounts.toString)
  }
}
```

## License

```
Copyright ScalikeJDBC committers
Apache License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.html
```
