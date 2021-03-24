package module5.abiturientService

import cats.effect.{ExitCode => CatsExitCode}
import module5.abiturientService.configuration.Configuration
import module5.abiturientService.grpcClient.{admissionPlanServiceClient, authServiceClient}
import module5.abiturientService.routes.AbiturientAPI
import module5.abiturientService.services.AbiturientRequestTopic
import module5.abiturientService.services.AbiturientRequestTopic.AbiturientRequestTopic
import module5.addmissionPlanService.ZioAddmissionPlanService.AdmissionPlanAPIClient
import module5.authService.ZioAuthService.AuthServiceAPIClient
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.interop.catz._
import zio.{RIO, ZIO, _}

object AbiturientApp extends App{


  type AbiturientAppEnv = Configuration with Clock with httpClient.HttpClient with AbiturientRequestTopic with AdmissionPlanAPIClient
    with AuthServiceAPIClient with Console with Blocking

  type AppTask[A] = RIO[AbiturientAppEnv, A]



  val abiturientAppEnv = Configuration.live ++ httpClient.HttpClient.live ++ admissionPlanServiceClient.live ++ authServiceClient.live ++ AbiturientRequestTopic.live

  val server: ZIO[AbiturientAppEnv, Throwable, Unit] = for{
    cfg <- configuration.load
    abiturientRoutes = new AbiturientAPI[AbiturientAppEnv]().routes
    httpApp = Router("" -> abiturientRoutes).orNotFound
    server <- ZIO.runtime[AbiturientAppEnv].flatMap { implicit rts =>
      val ec = rts.platform.executor.asEC
      BlazeServerBuilder[AppTask](ec)
        .bindHttp(cfg.abiturientApi.port, cfg.abiturientApi.host)
        .withHttpApp(httpApp)
        .serve
        .compile[AppTask, AppTask, CatsExitCode]
        .drain
    }
  } yield server


  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    AbiturientApp.server
      .provideSomeLayer[ZEnv](abiturientAppEnv)
      .tapError(err => zio.console.putStrLn(s"Execution failed with: $err"))
      .exitCode
  }

}
