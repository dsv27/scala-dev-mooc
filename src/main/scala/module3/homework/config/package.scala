package module3.homework

import java.nio.file.Paths

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.Task

package object config {

  case class AppConfig(appName: String, appUrl: String)

  val load: Task[AppConfig] =
    Task.effect(ConfigSource.file(Paths.get("src/main/resources/custom.conf")).loadOrThrow[AppConfig])
}
