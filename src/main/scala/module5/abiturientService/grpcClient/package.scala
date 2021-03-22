package module5.abiturientService

import io.grpc.ManagedChannelBuilder
import module5.addmissionPlanService.ZioAddmissionPlanService.AdmissionPlanAPIClient
import module5.authService.ZioAuthService.AuthServiceAPIClient
import scalapb.zio_grpc.ZManagedChannel

package object grpcClient {

  object admissionPlanServiceClient{
    private val channel = ZManagedChannel(
        ManagedChannelBuilder
        .forAddress("localhost", 3333)
        .usePlaintext()
    )

    val live = AdmissionPlanAPIClient.live(channel)
  }

  object authServiceClient{
    private val channel = ZManagedChannel(
      ManagedChannelBuilder.forAddress("localhost", 3334)
        .usePlaintext()
    )

    val live = AuthServiceAPIClient.live(channel)
  }
}
