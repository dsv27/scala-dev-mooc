package module3.zioEffect

import zio.Cause.{Both, Die, Internal, Then}
import zio.console.{Console, putStrLn}
import zio.{Cause, IO, UIO, URIO, ZIO}

object zioErrorHandling {

  sealed trait Cause[+E]

  object Cause {

    final case class Fail[E](e: E) extends Cause[E]

    final case class Die(t: Throwable) extends Cause[Nothing]

  }


  case class ZIO[-R, +E, +A](run: R => Either[E, A]) {
    self =>

    def foldM[R1 <: R, E1, B](
                               failure: E => ZIO[R1, E1, B],
                               success: A => ZIO[R1, E1, B]
                             ): ZIO[R1, E1, B] =
      ZIO(r => self.run(r).fold(failure, success).run(r))


    def orElse[R1 <: R, E1, A1 >: A](other: ZIO[R1, E1, A1]): ZIO[R1, E1, A1] = foldM(
      _ => other,
      v => ZIO(_ => Right(v))
    )

    /**
     * Реализовать метод, котрый будет игнорировать ошибку в случае падения,
     * а в качестве результата возвращать Option
     */
    def option: ZIO[R, Nothing, Option[A]] = foldM(
      _ => ZIO(_ => Right(None)),
      v => ZIO(_ => Right(Some(v)))
    )

    /**
     * Реализовать метод, котрый будет работать с каналом ошибки
     */
    def mapError[E1](f: E => E1): ZIO[R, E1, A] = foldM(
      e => ZIO(_ => Left(f(e))),
      v => ZIO( _ => Right(v))
    )


  }


  val a = ZIO[Any, String, Int](_ => Left("Ooops"))


  sealed trait UserRegistrationError

  case object InvalidEmail extends UserRegistrationError

  case object WeakPassword extends UserRegistrationError

  lazy val checkEmail: IO[InvalidEmail.type, String] = zio.ZIO.fail(InvalidEmail)
  lazy val checkPassword: IO[WeakPassword.type, String] = zio.ZIO.fail(WeakPassword)

  lazy val userRegistrationCheck: zio.ZIO[Any, UserRegistrationError, (String, String)] = checkEmail.zip(checkPassword)


  lazy val io1: IO[String, String] = ???

  lazy val io2: IO[Int, String] = ???

  val _: zio.ZIO[Any, Any, (String, String)] = io1.zip(io2)

  /**
   * 1. Какой будет тип на выходе, если мы скомбинируем эти два эффекта с помощью zip
   */


  lazy val io3: zio.ZIO[Any, Either[String, Int], (String, String)] = io1.mapError(Left(_)).zip(io2.mapError(Right(_)))

  def either: Either[String, Int] = ???

  def errorToErrorCode(str: String): Int = ???


  lazy val effFromEither: IO[String, Int] = zio.ZIO.fromEither(either)

  /**
   * Залогировать ошибку, не меняя ее тип и тип возвращаемого значения
   */
  lazy val _: ZIO[Console, String, String] = effFromEither.tapError(err => putStrLn(err))

  /**
   * Изменить ошибку
   */

  lazy val _: zio.ZIO[Any, Int, Int] = effFromEither.mapError(errStr => errorToErrorCode(errStr))


  lazy val effEitherErrorOrResult: URIO[Any, Either[String, Int]] = effFromEither.either

  // Вернуть effEitherErrorOrResult обратно
  lazy val _: IO[String, Int] = effEitherErrorOrResult.absolve

}