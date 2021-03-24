package module5.admissionPlanService

import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import zio.{ExitCode, RIO, URIO, ZEnv, ZIO}
import zio.clock.Clock
import zio.interop.catz._
import zio._
import cats.effect.{ExitCode => CatsExitCode}
import module5.abiturientService.httpClient.HttpClient
import module5.admissionPlanService.configuration.Configuration
import module5.admissionPlanService.routes.AdmissionPlanAPI
import module5.admissionPlanService.services.AbiturientRequestConsumer
import org.http4s.implicits._
import zio.blocking.Blocking
import zio.console.Console
import zio.kafka.consumer.Consumer

object AdmissionPlanApp extends App {
  type AdmissionPlanAppEnv = Configuration with Clock with HttpClient with Console with Blocking
  type AppTask[A] = RIO[AdmissionPlanAppEnv, A]


  val admissionPlanAppEnv = Configuration.live ++ HttpClient.live

  val server: ZIO[AdmissionPlanAppEnv, Throwable, Unit] = for{
    cfg <- configuration.load
    _ <- AbiturientRequestConsumer.consumeRequests.forkDaemon
    abiturientRoutes = new AdmissionPlanAPI[AdmissionPlanAppEnv]().routes
    httpApp = Router("" -> abiturientRoutes).orNotFound
    server <- ZIO.runtime[AdmissionPlanAppEnv].flatMap { implicit rts =>
      val ec = rts.platform.executor.asEC
      BlazeServerBuilder[AppTask](ec)
        .bindHttp(cfg.admissionPlanApi.port, cfg.admissionPlanApi.host)
        .withHttpApp(CORS(httpApp))
        .serve
        .compile[AppTask, AppTask, CatsExitCode]
        .drain
    }
  } yield server


  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    AdmissionPlanApp.server
      .provideSomeLayer[ZEnv](admissionPlanAppEnv)
      .tapError(err => zio.console.putStrLn(s"Execution failed with: $err"))
      .exitCode
  }
}
