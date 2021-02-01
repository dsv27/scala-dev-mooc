package module3.zioEffect

import java.io.IOException

import zio.{Fiber, IO, Ref, UIO, URIO, ZIO}
import zio.clock.Clock
import zio.console.{Console, getStrLn, putStrLn}
import zio.duration._
import zio.internal.Executor

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object zioConcurrency {

  /** **
   * Fiber - это легковесный эквивалент потока
   * Как и поток он моделирует исполняемое вычисление, а все инструкции в рамках одного Fiber
   * выполняются последовательно
   *
   * Однако в отличии от потоков, они легковесны и стоят дешево. Поэтому мы можем спокойно создавать 10-ки
   * а то и 100 тысяч Fiber-ов
   *
   * В отличии от потоков их можно безопастно прерывать и джоинить без блокировки
   */


  /**
   * Создать эффект который печатает в консоль GetExchangeRatesLocation1
   */
  lazy val getExchangeRatesLocation1 = ???

  /**
   * Создать эффект который печатает в консоль GetExchangeRatesLocation2
   */
  lazy val getExchangeRatesLocation2 = ???

  /**
   * Эффект который все что делает, это спит заданное кол-во времени, в данном случае 1 секунду
   */

  val sleep1 = ZIO.sleep(1 second)


  /**
   * Изменить getExchangeRates, так чтобы он спал 1 секунду, а затем печатал в консоль
   */


  /**
   * Написать эффект котрый получит курсы из обеих локаций
   */
  lazy val getFrom2Locations = ???

  /**
   * Получние информации от сервиса занимает 1 секунду
   */
  def getFromService(ref: Ref[Int]) = for {
    count <- ref.getAndUpdate(_ + 1)
    _ <- putStrLn(s"GetFromService - ${count}") *> ZIO.sleep(1 seconds)
  } yield ()

  /**
   * Отправка в БД занимает в общем 5 секунд
   */
  def sendToDB(ref: Ref[Int]): ZIO[Clock with Console, Exception, Unit] = for {
    count <- ref.getAndUpdate(_ + 1)
    _ <- ZIO.sleep(5 seconds) *> putStrLn(s"SendToDB - ${count}")
  } yield ()


  /**
   * Написать программу, которая конкурентно вызывает выше описанные сервисы
   * и при этом обеспечивает сквозную нумерацию вызовов
   */
  lazy val app1 = ???


  /**
   * Lock
   */


  // Правило 1
  lazy val doSomething2: UIO[Unit] = ???
  lazy val doSomethingElse2: UIO[Unit] = ???

  lazy val executor: Executor = ???
  lazy val effect = for {
    _ <- doSomething2.fork
    _ <- doSomethingElse2
  } yield ()

  lazy val result = effect.lock(executor)


  // Правило 2
  lazy val executor1: Executor = ???
  lazy val executor2: Executor = ???

  lazy val effect2 = for {
    _ <- doSomething2.lock(executor2).fork
    _ <- doSomethingElse2
  } yield ()

  lazy val result2 = effect.lock(executor1)


  /**
   * простая гонка эффектов
   */
  val res1: URIO[Clock, Int] = ZIO.sleep(3.second).as(4)

  val res2: URIO[Clock, Int] = ZIO.sleep(1.second).as(7)


}