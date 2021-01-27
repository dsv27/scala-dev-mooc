package module3.zioEffect

import zio.Cause.{Die, Internal}
import zio.console.Console
import zio.{Cause, IO, UIO, URIO, ZIO}

object zioErrorHandling {

  case class ZIO[-R, E, A](run: R => Either[E, A]){ self =>

    def foldM[R1 <: R, E1, B](
                               failure: E => ZIO[R1, E1, B],
                               success: A => ZIO[R1, E1, B]
                             ): ZIO[R1, E1, B] = ???


    def orElse[R1 <: R, E1, A1 >: A](other: ZIO[R1, E1, A1]): ZIO[R1, E1, A1] = ???

    def option[R1 <: R, E1 >: E]: ZIO[R1, Nothing, Option[A]] = ???

    def mapError[E1 >: E](f: E => E1): ZIO[R, E1, A] = ???
  }




  val io1: IO[String, String] = ???

  val io2: IO[Int, String] = ???

  /**
   * 1. Какой будет тип на выходе, если мы скомбинируем эти два эффекта с помощью zip
   */

  val io3 = ???

  def either: Either[String, Int] = ???
  def errorToErrorCode(str: String): Int = ???


  val effFromEither: IO[String, Int] = ???

  /**
   * Залогировать ошибку, не меняя ее тип и тип возвращаемого значения
   */
  val _: ZIO[Console, String, String] = ???

  /**
   * Изменить ошибку
   */

  val _: zio.ZIO[Any, Int, Int] = effFromEither.mapError(errStr => errorToErrorCode(errStr))


  val effEitherErrorOrResult: URIO[Any, Either[String, Int]] = ???

  // Вернуть effEitherErrorOrResult обратно
  val _: IO[String, Int] = ???

}