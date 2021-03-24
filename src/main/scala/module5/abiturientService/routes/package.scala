package module5.abiturientService

import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import module5.abiturientService.configuration.Configuration
import module5.abiturientService.dtos.AdmissionPlanResponse.AbiturientRequestAccepted
import module5.abiturientService.dtos.{AbiturientRequestDTO, AdmissionPlanResponse}
import module5.abiturientService.httpClient.HttpClient
import module5.abiturientService.services.AbiturientRequestTopic
import module5.abiturientService.services.AbiturientRequestTopic.AbiturientRequestTopic
import module5.addmissionPlanService.AbiturientRequest
import module5.addmissionPlanService.ZioAddmissionPlanService.AdmissionPlanAPIClient
import module5.authService.AuthRequest
import module5.authService.ZioAuthService.AuthServiceAPIClient
import org.apache.kafka.clients.producer.RecordMetadata
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{Accept, Authorization}
import org.http4s._
import org.http4s.client.Client
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.interop.catz._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import zio.{Has, RIO, Task, ZIO, ZLayer}


package object routes {
  type APIEnv = Configuration with HttpClient with Clock with AdmissionPlanAPIClient with Console with AuthServiceAPIClient with Blocking with AbiturientRequestTopic

  final class AbiturientAPI[R <: APIEnv]{

    type AbiturientAPITask[A] = RIO[R, A]

    val dsl = new Http4sDsl[AbiturientAPITask]{}

    implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[AbiturientAPITask, A] = jsonOf[AbiturientAPITask, A]
    implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[AbiturientAPITask, A] = jsonEncoderOf[AbiturientAPITask, A]

    import dsl._

    def create: RIO[Configuration, Unit] = Task()

    def routes: HttpRoutes[AbiturientAPITask] = HttpRoutes.of[AbiturientAPITask]{
      case req @ POST -> Root / "api" / "v1" / "abiturient" =>
        req.decode[AbiturientRequestDTO] { abiturientRequestDTO =>

          // Реализовать запрос к сервису AdmissionPlan
          val response = for{
           client <- httpClient.client
           uri = Uri.fromString("http://localhost:8081/api/v2/admissionPlan").toOption.get
           request = Request[Task](
              method = POST,
              uri = uri,
              headers = Headers(List(Accept(MediaType.application.json)))
           ).withEntity(abiturientRequestDTO)
            result <- client.expect[AdmissionPlanResponse](request)

          } yield result

          response.foldM(ex => InternalServerError(ex.getMessage), {
              case a @ AbiturientRequestAccepted(_) => Ok(a)
              case a@AdmissionPlanResponse.AbiturientRequestRejected(_) => Ok(a)
            }
          )
        }

      case req @ POST -> Root / "api" / "v2" / "abiturient" =>
        req.decode[AbiturientRequestDTO] { abiturientRequestDTO =>
          val resp = for {
            r <- AdmissionPlanAPIClient.checkAbiturientRequest(AbiturientRequest(abiturientRequestDTO.fio))
            _ <- putStrLn(r.toString)
          } yield r
          resp.foldM(ex => InternalServerError(ex.getCause.getMessage), v => Ok(v.toString))
        }

      case req @ POST -> Root / "api" / "v1" / "abiturient" / "request" / "acceptance"  =>
        req.decode[AdmissionPlanResponse] { admissionPlanResponse =>
          println(admissionPlanResponse)
          Ok(Task("Updated"))
        }

      case req @ POST -> Root / "api" / "v3" / "abiturient" =>
        req.decode[AbiturientRequestDTO] { abiturientRequestDTO =>

          val resp = AbiturientRequestTopic.send(abiturientRequestDTO)

          resp.foldM(ex => InternalServerError(ex.getCause.getMessage), r =>
            Ok(r.toString)
          )
        }

    }
  }
}
