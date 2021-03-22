package module5.admissionPlanService

import pureconfig.ConfigSource
import zio.{Task, ULayer, ZIO, ZLayer}
import pureconfig.generic.auto._

package object configuration {
  case class Config(admissionPlanApi: ApiConfig)
  case class ApiConfig(host: String, port: Int, baseApiUri: String)

  type Configuration = zio.Has[Configuration.Service]
  object Configuration{
    trait Service {
      def  load: Task[Config]
    }


    trait Live extends Configuration.Service {
      val load: Task[Config] =
        Task.effect(ConfigSource.default.loadOrThrow[Config])
    }

    val live: ULayer[Configuration] = ZLayer.succeed(new Live {})

  }

  // accessible pattern
  val load: ZIO[Configuration, Throwable, Config] = ZIO.accessM(_.get.load)
}
