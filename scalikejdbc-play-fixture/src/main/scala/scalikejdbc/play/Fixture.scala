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
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package scalikejdbc.play

import scala.io.Source
import scala.io.Codec
import java.io.File

case class Fixture(file: File) {

  private def script: String = Source.fromFile(file)(Codec.UTF8).mkString

  private def isUpsMarker(s: String): Boolean = s.matches("""^#.*!Ups.*$""")

  private def isDownsMarker(s: String): Boolean = s.matches("""^#.*!Downs.*$""")

  def upScript: String =
    script
      .linesIterator
      .dropWhile { line => !isUpsMarker(line) }
      .dropWhile { line => isUpsMarker(line) }
      .takeWhile { line => !isDownsMarker(line) }
      .mkString("\n")

  def downScript: String =
    script
      .linesIterator
      .dropWhile { line => !isDownsMarker(line) }
      .dropWhile { line => isDownsMarker(line) }
      .mkString("\n")
}
