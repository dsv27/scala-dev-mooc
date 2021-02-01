package module1

import module3.zioEffect.{functionalProgram, simpleProgram, zioConcurrency, zioErrorHandling}
import module3.zioMonad.{toyModel, zioOperators, zioRecursion}
import org.slf4j.LoggerFactory
import zio.Cause.{Die, Fail}
import zio.{Exit, IO, ZIO, clock, console}
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.duration.durationInt

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object App {

  val logger = LoggerFactory.getLogger("ZIO APP")

  type UserProfileError
  type UserProfile
  type UserId
  type FileServiceError
  type Files

  def main(args: Array[String]): Unit = {


    zio.Runtime.default.unsafeRun(zioRecursion.readIntOrRetry)

//    Thread.sleep(10000)
  }
}

