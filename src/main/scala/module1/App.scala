package module1

import java.io.IOException

import module3.homework
import module3.zioEffect.{catsIOvsZIO, functionalProgram, simpleProgram, zioConcurrency, zioErrorHandling}
import module3.zioMonad.{toyModel, zioOperators, zioRecursion}
import module3.zioService.{dbService, userService}
import module3.zioService.dbService.DbService
import module3.zioService.di.DBService
import module3.zioService.userService.UserService
import org.slf4j.LoggerFactory
import zio.Cause.{Die, Fail}
import zio.{Exit, IO, RIO, Task, ZIO, ZLayer, clock, console}
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.duration.durationInt

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import scala.language.postfixOps

object App {

  val logger = LoggerFactory.getLogger("ZIO APP")

  type UserProfileError
  type UserProfile
  type UserId
  type FileServiceError
  type Files

  def main(args: Array[String]): Unit = {



//    zio.Runtime.default.unsafeRun(???)

//    Thread.sleep(2000)
  }
}

