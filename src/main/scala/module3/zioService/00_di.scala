package module3.zioService

import java.net.http.WebSocket

import module1.App.UserId
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{IO, Task, ZIO, ZLayer}

import scala.concurrent.{ExecutionContext, Future}

object di {

  type Query
  type DBError
  type QueryResult
  type Email


  trait DService{
    def tx(query: Query): IO[DBError, QueryResult]
  }

  trait EmailService{
    def makeEmail(email: String, body: String): Task[Email]
    def sendEmail(email: Email): Task[Unit]
  }

  type MyEnv = Clock with Console with Random

  def e1: ZIO[Clock with Console with Random, Nothing, Unit] = ???
  def e2: ZIO[MyEnv, Nothing, Unit] = ???

  /**
   * Написать ZIO программу которая выполнит запрос и отправит email
   */

  lazy val queryAndNotify = ???

  lazy val services: DService with EmailService = ???

  def services(emailService: EmailService): EmailService with DService = ???

  lazy val emailService: EmailService = ???

  // provide
  lazy val e3 = ???

  // provide some
  lazy val e4 = ???

  // provide
  lazy val e5 = ???

  lazy val servicesLayer: ZLayer[Any, Nothing, DService with EmailService] = ???

  lazy val dServiceLayer: ZLayer[Any, Nothing, DService] = ???

  // provide layer
  lazy val e6 = ???

  // provide some layer
  lazy val e7 = ???






}