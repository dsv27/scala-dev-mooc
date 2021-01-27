package module1

import module3.zioEffect.{functionalProgram, simpleProgram, zioConcurrency}
import module3.zioMonad.toyModel
import org.slf4j.LoggerFactory
import zio.Cause.{Die, Fail}
import zio.{Exit, IO, ZIO, clock, console}
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.duration.durationInt

import scala.language.postfixOps

object App {

  val logger = LoggerFactory.getLogger("ZIO APP")



  def main(args: Array[String]): Unit = {
//    functionalProgram.interpret(functionalProgram.consoleProgram2)

    val pr = toyModel.app.run
    pr()
  }
}
