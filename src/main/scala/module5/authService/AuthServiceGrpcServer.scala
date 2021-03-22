package module5.authService

import io.grpc.Status
import module5.authService.ZioAuthService.AuthServiceAPI
import scalapb.zio_grpc.{ServerMain, ServiceList}
import zio.{Task, ZIO}

object AuthServiceImpl extends AuthServiceAPI {
  override def isAuth(request: AuthRequest): ZIO[Any, Status, AuthResponse] = Task.effect {
    println(request)
    if (request.token == "Bearer xxx") AuthResponse(1)
    else AuthResponse(-1)
  }.mapError(_ => Status.INTERNAL)
}

object AuthServiceGrpcServer extends ServerMain{
  override def services: ServiceList[zio.ZEnv] = ServiceList.add(AuthServiceImpl)

  override def port: Int = 3334
}
