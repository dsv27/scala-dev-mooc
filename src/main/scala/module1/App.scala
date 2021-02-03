package module1

import java.io.IOException

import module3.zioEffect.{catsIOvsZIO, functionalProgram, simpleProgram, zioConcurrency, zioErrorHandling}
import module3.zioMonad.{toyModel, zioOperators, zioRecursion}
import org.slf4j.LoggerFactory
import zio.Cause.{Die, Fail}
import zio.{Exit, IO, RIO, Task, ZIO, clock, console}
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


    lazy val readLine1: RIO[Console, Int] = console.getStrLn.mapEffect(_.toInt)
    lazy val readLine2 = ZIO.fail(new NumberFormatException)
    lazy val readLine3 = ???

    zio.Runtime.default.unsafeRun(readLine1.orElse(putStrLn("Nok")))

//    Thread.sleep(10000)
  }
}

