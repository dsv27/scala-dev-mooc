package module3.zioService

import module3.zioService.di.Query
import zio.console.{Console, putStrLn}
import zio.{Has, IO, ULayer, URIO, ZIO, ZLayer}


package object dbService {

  type DbService = Has[DbService.Service]



  object DbService{

    trait Service{
      def tx(query: String): IO[String, String]
    }

    val live = ZLayer.succeed(new Service {
      override def tx(query: String): IO[String, String] = ???
    })
  }
  def tx(str: String): ZIO[DbService, String, String] = ZIO.accessM[dbService.DbService](_.get.tx(str))
}
