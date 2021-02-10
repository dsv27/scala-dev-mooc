package module3.zioService

import java.net.http.WebSocket

import javax.management.Query
import module1.App.UserId
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{Has, IO, Task, ZIO, ZLayer}

import scala.concurrent.{ExecutionContext, Future}

object di {

  type Query
  type DBError
  type QueryResult
  type Email


  trait DBService{
      def tx(query: Query): IO[DBError, QueryResult]
  }

  trait EmailService{
    def makeEmail(email: String, body: String): Task[Email]
    def sendEmail(email: Email): Task[Unit]
  }

  val dbModule: DBService = ???

  val emailModule: EmailService = ???


  val combined: DBService with EmailService = ???



  type MyEnv = Clock with Console with Random

  def e1: ZIO[Clock with Console with Random, Nothing, Unit] = ???
  def e2: ZIO[MyEnv, Nothing, Unit] = ???

  /**
   * Написать ZIO программу которая выполнит запрос и отправит email
   */
    val ef1: ZIO[EmailService with DBService, Any, Unit] = for {
      dbService <- ZIO.environment[DBService]
      emailService <- ZIO.environment[EmailService]
      email <- emailService.makeEmail("", "")
      query: Query = ???
      result <- dbService.tx(query)
      _ <- emailService.sendEmail(email)
    } yield ()

    val ef2: ZIO[EmailService, Throwable, Unit] = ???

  val _: ZIO[EmailService with DBService, Any, QueryResult] = ef1 <* ef2


  lazy val queryAndNotify: ZIO[EmailService with DBService, Any, Unit] = ???

  lazy val services: DBService with EmailService = ???

  def services(emailService: EmailService): EmailService with DBService = ???

  lazy val dBService: DBService = ???

  lazy val emailService: EmailService = ???

  // provide
  lazy val e3: IO[Any, Unit] = queryAndNotify.provide(services)

  // provide some
  lazy val e4: ZIO[EmailService, Any, Unit] =
    queryAndNotify.provideSome[EmailService](es => services(es))

  // provide
  lazy val e5 = ???

  lazy val servicesLayer: ZLayer[Any, Nothing, DBService with EmailService] = ???

  lazy val dbServiceLayer: ZLayer[Any, Nothing, DBService] = ???

  // provide layer
//  lazy val e6 = queryAndNotify.provideLayer(servicesLayer)

  // provide some layer
//  lazy val e7 = queryAndNotify.provideSomeLayer[EmailService](dbServiceLayer)




  trait A
  trait B
  trait C

  type A_ = Has[A]
  type B_ = Has[B]
  type C_ = Has[C]

  val zlayerA: ZLayer[Any, Nothing, A_] = ???
  val zlayerB: ZLayer[Any, Nothing, B_] = ???
  val zlayerC: ZLayer[Any, Nothing, C_] = ???

  val _: ZLayer[Any, Nothing, A_ with B_] = zlayerA >>> (zlayerB ++ zlayerA)



}