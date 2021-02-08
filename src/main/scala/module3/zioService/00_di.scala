package module3.zioService

import java.net.http.WebSocket

import module1.App.UserId
import zio.{IO, Task}

import scala.concurrent.{ExecutionContext, Future}

object di {

  type Query
  type DBError
  type QueryResult
  type Email

}