
package module5.admissionPlanService

import io.grpc.Status
import module5.addmissionPlanService.{AbiturientRequest, AdmissionPlanResponse}
import module5.addmissionPlanService.ZioAddmissionPlanService.AdmissionPlanAPI
import scalapb.zio_grpc.{ServerMain, ServiceList}
import zio.ZIO

object AdmissionPlanGrpcServer extends ServerMain{

  object ZioAbiturientServiceImpl extends AdmissionPlanAPI {
    override def checkAbiturientRequest(request: AbiturientRequest): ZIO[Any, Status, AdmissionPlanResponse] =
      ZIO.effect(AdmissionPlanResponse(1)).mapError(ex => Status.INTERNAL)
  }
  override def services: ServiceList[zio.ZEnv] = ServiceList.add(ZioAbiturientServiceImpl)

  override def port: Int = 3333
}

