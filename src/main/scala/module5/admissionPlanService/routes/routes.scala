package module5.admissionPlanService

import io.circe.{Decoder, Encoder}
import module5.admissionPlanService.dtos.AdmissionPlanResponse
import module5.abiturientService.httpClient
import module5.admissionPlanService.configuration.Configuration
import module5.admissionPlanService.dtos.AbiturientRequestDTO
import module5.admissionPlanService.dtos.AdmissionPlanResponse.AbiturientRequestAccepted
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Accept
import org.http4s.{EntityDecoder, EntityEncoder, Headers, HttpRoutes, MediaType, Request, Uri}
import zio.clock.Clock
import zio.console.Console
import zio.duration.durationInt
import zio.interop.catz._
import zio.interop.console.cats.putStrLn
import zio.{RIO, Schedule, Task, ZIO}
import scala.language.postfixOps


package object routes {
  type APIEnv = Configuration with httpClient.HttpClient with Clock with Console

  final class AdmissionPlanAPI[R <: APIEnv]{

    type AdmissionPlanAPITask[A] = RIO[R, A]

    val dsl = new Http4sDsl[AdmissionPlanAPITask]{}

    implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[AdmissionPlanAPITask, A] = jsonOf[AdmissionPlanAPITask, A]
    implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[AdmissionPlanAPITask, A] = jsonEncoderOf[AdmissionPlanAPITask, A]

    import dsl._

    def create: RIO[Configuration, Unit] = Task()

    def routes: HttpRoutes[AdmissionPlanAPITask] = HttpRoutes.of[AdmissionPlanAPITask]{
      case req @ POST -> Root / "api" / "v1" / "admissionPlan" =>
        req.decode[AbiturientRequestDTO] { abiturientRequestDTO =>
          Created(Task[AdmissionPlanResponse](AbiturientRequestAccepted(1)))
        }

      case req @ POST -> Root / "api" / "v2" / "admissionPlan" =>
        req.decode[AbiturientRequestDTO] { abiturientRequestDTO =>

          val response = for {
            client <- httpClient.client
            uri = Uri.fromString("http://localhost:8080/api/v1/abiturient/request/acceptance").toOption.get
            request = Request[Task](
              method = POST,
              uri = uri,
              headers = Headers(List(Accept(MediaType.application.json)))
            ).withEntity[AdmissionPlanResponse](AbiturientRequestAccepted(2))
          result <- client.expect[String](request)
          } yield result

          for{
            fiber <- response.delay(5 seconds).forkDaemon
            response <- Created(AdmissionPlanResponse.accepted(2))
          }yield response

          response.foldM(ex => InternalServerError(ex.getMessage), v => Ok(v.toString))

        }
    }
  }
}
