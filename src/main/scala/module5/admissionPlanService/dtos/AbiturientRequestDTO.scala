package module5.admissionPlanService.dtos

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import zio.Task
import zio.interop.catz._

case class AbiturientRequestDTO(fio: String)

object AbiturientRequestDTO{
  implicit val codec: Codec[AbiturientRequestDTO] = deriveCodec
}

sealed trait AdmissionPlanResponse

object AdmissionPlanResponse {
  def accepted(id: Int): AdmissionPlanResponse = AbiturientRequestAccepted(id)
  case class AbiturientRequestAccepted(id: Int) extends AdmissionPlanResponse

  object AbiturientRequestAccepted{
//    implicit val codec: Codec[AbiturientRequestAccepted] = deriveCodec
//    implicit val decoder: EntityDecoder[Task, AbiturientRequestAccepted] = jsonOf[Task, AbiturientRequestAccepted]
//    implicit val encoder: EntityEncoder[Task, AbiturientRequestAccepted] = jsonEncoderOf[Task, AbiturientRequestAccepted]
  }

  case class AbiturientRequestRejected(code: String) extends AdmissionPlanResponse

  object AbiturientRequestRejected {
//    implicit val codec: Codec[AbiturientRequestRejected] = deriveCodec
  }

//  implicit private lazy val circeConfig: Configuration = Configuration.default.withDefaults.withDiscriminator("type")
  implicit val useDefaultValues = Configuration.default.withDefaults

  implicit val codec: Codec[AdmissionPlanResponse] = deriveConfiguredCodec[AdmissionPlanResponse]
  implicit val decoder: EntityDecoder[Task, AdmissionPlanResponse] = jsonOf[Task, AdmissionPlanResponse]
  implicit val encoder: EntityEncoder[Task, AdmissionPlanResponse] = jsonEncoderOf[Task, AdmissionPlanResponse]
}