package module3.zioService

import module3.zioService.dbService.{DbService, tx}
import module3.zioService.di.Email
import zio.console.{Console, putStrLn}
import zio.{Has, Task, ULayer, ZIO, ZLayer}

package object emailService {
  type EmailService = Has[EmailService.Service]

  object EmailService{
    trait Service{
      def sendEmail(email: Email): Task[Unit]
    }

    val live: ULayer[EmailService] = ZLayer.succeed(new Service {
      override def sendEmail(email: Email): Task[Unit] = Task.effect(println(email))
    })
  }

  def sendEmail(email: Email): ZIO[EmailService, Throwable, Unit] =
    ZIO.accessM[EmailService](_.get.sendEmail(email))

}
