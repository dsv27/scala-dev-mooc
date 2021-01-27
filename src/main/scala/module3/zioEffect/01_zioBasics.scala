package module3.zioMonad

import java.io.IOException

import module3.zioMonad.toyModel.ZIO.fail
import zio.clock.Clock
import zio.console.{Console, getStrLn, putStrLn}
import zio.{IO, RIO, Task, UIO, URIO, ZIO}

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try


/** **
 * ZIO - функциональный эффект
 */


/** **
 * ZIO[-R, +E, +A] ----> R => Either[E, A]
 *
 *
 */


object toyModel {

  final case class ZIO[-R, +E, +A](run: R => Either[E, A]) {
    self =>

    def map[B](f: A => B): ZIO[R, E, B]  = ZIO(r => self.run(r).map(f))

    def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] = ZIO(
      r => self.run(r).fold(ZIO.fail, f).run(r)
    )

  }

  object ZIO {

    def effect[A](a: => A): ZIO[Any, Throwable, A] = {
      try {
        ZIO(_ => Right(a))
      } catch { case e =>
        ZIO(_ =>Left(e))
      }
    }

    def fail[E](e: E): ZIO[Any, E, Nothing] = ZIO(_ => Left(e))

  }

  /** *
   * Напишите консольное echo приложение с помощью нашего игрушечного ZIO
   */
  val app: ZIO[Any, Throwable, Unit] = for{
    str <- ZIO.effect(StdIn.readLine())
    _ <- ZIO.effect(println(str))
  } yield ()



}

object zioConstructors {

  val _: ZIO[Any, Nothing, Int] = ZIO.succeed(7)

  val _: ZIO[Any, Throwable, Int] = ZIO.effect(7/0)

  val _: ZIO[Any, Nothing, Unit] = ZIO.effectTotal(println(""))

  val f: Future[Int] = ???

  val _: ZIO[Any, Throwable, Int] = ZIO.fromFuture(ec => f)

  val _: ZIO[Any, String, Int] = ???

  val _: ZIO[Any, Throwable, Int] = ???

  ZIO.unit

  ZIO.none

  ZIO.never

  val _: ZIO[Any, Nothing, Nothing] = ZIO.die(new Throwable("Died"))

  val _: ZIO[Any, String, Nothing] = ZIO.fail(7)

}

object zioTypeAliases {
  type Error
  type Environment

  val _: IO[Error, Int] = ???
  val _: Task[Int] = ???
  val _: RIO[Environment, Int] = ???
  val _: URIO[Environment, Int] = ???
  val _: UIO[Int] = ???
}


object zioOperators {

  /** *
   *
   * 1. Создать ZIO эффект который будет читать строку из консоли
   */

  lazy val readLine: Task[String] = ZIO.effect(StdIn.readLine())

  /** *
   *
   * 2. Создать ZIO эффект который будет писать строку в консоль
   */

  def writeLine(str: String): Task[Unit] = ZIO.effect(println(str))

  /** *
   * 3. Создать ZIO эффект котрый будет трансформировать эффект содержащий строку в эффект содержащий Int
   */

  lazy val lineToInt: ZIO[Any, Throwable, Int] = readLine.map(_.toInt)

  /** *
   * 3.Создать ZIO эффект, который будет работать как echo для консоли
   *
   */

  lazy val echo = for {
    str <- readLine
    _ <- writeLine(str)
  } yield ()

  /**
   * Создать ZIO эффект, который будет привествовать пользователя и говорить, что он работает как echo
   */

  lazy val greetAndEcho: ZIO[Any, Throwable, (Unit, Unit)] = writeLine("Привет, я эхо").zip(echo)

  lazy val a: Task[Int] = ???
  lazy val b: Task[String] = ???

  lazy val ab1: ZIO[Any, Throwable, (Int, String)] = ???

  lazy val ab2: ZIO[Any, Throwable, Int] = ???

  lazy val ab3: ZIO[Any, Throwable, String] = ???

  // as


  def readFile(fileName: String): ZIO[Any, IOException, String] = ???

  lazy val _: URIO[Any, String] = readFile("test.txt").orDie

}


object zioRecursion {

  /** **
   * Написать программу, которая считывает из консоли Int введнный пользователем,
   * а в случае ошибки, сообщает о некорректном вводе, и просит ввести заново
   *
   */

  val readInt: ZIO[Console, Throwable, Int] = ???


  val readIntOrRetry: ZIO[Console, Throwable, Int] = ???

}




