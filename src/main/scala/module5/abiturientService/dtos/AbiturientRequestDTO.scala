package module5.abiturientService.dtos

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import zio.Task
import zio.interop.catz._

case class AbiturientRequestDTO(fio: String)

object AbiturientRequestDTO{
  implicit val codec: Codec[AbiturientRequestDTO] = deriveCodec

  implicit val decoder: EntityDecoder[Task, AbiturientRequestDTO] = jsonOf[Task, AbiturientRequestDTO]
  implicit val encoder: EntityEncoder[Task, AbiturientRequestDTO] = jsonEncoderOf[Task, AbiturientRequestDTO]
}

sealed trait AdmissionPlanResponse

object AdmissionPlanResponse {
  case class AbiturientRequestAccepted(id: Int) extends AdmissionPlanResponse

  object AbiturientRequestAccepted{
//    implicit val codec: Codec[AbiturientRequestAccepted] = deriveCodec
  }

  case class AbiturientRequestRejected(code: String) extends AdmissionPlanResponse

  object AbiturientRequestRejected {
//    implicit val codec: Codec[AbiturientRequestRejected] = deriveCodec
  }

  implicit private lazy val circeConfig: Configuration = Configuration.default/*.withDefaults.withDiscriminator("type")*/

  implicit val codec: Codec[AdmissionPlanResponse] = deriveConfiguredCodec[AdmissionPlanResponse]
  implicit val decoder: EntityDecoder[Task, AdmissionPlanResponse] = jsonOf[Task, AdmissionPlanResponse]
}
