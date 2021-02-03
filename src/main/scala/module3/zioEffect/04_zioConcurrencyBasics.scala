package module3.zioEffect

import java.io.IOException
import java.util.concurrent.TimeUnit

import zio.{Fiber, IO, Ref, UIO, URIO, ZIO, clock}
import zio.clock.{Clock, sleep}
import zio.console.{Console, getStrLn, putStr, putStrLn}
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



  // эфект содержит в себе текущее время
  val currentTime: URIO[Clock, Long] = clock.currentTime(TimeUnit.SECONDS)


  /**
   * Напишите эффект, который будет считать время выполнения любого эффекта
   */


   def printEffectRunningTime[R, E, A](zio: ZIO[R, E, A]) = for {
     start <- currentTime
     r <- zio
     finish <- currentTime
     _ <- putStrLn(s"Running time: ${finish - start}")
   } yield r


  val exchangeRates: Map[String, Double] = Map(
    "usd" -> 76.02,
    "eur" -> 91.27
  )

  /**
   * Создать эффект который печатает в консоль GetExchangeRatesLocation1
   */
  lazy val getExchangeRatesLocation1 = sleep(3 seconds) *> putStrLn("GetExchangeRatesLocation1")

  /**
   * Создать эффект который печатает в консоль GetExchangeRatesLocation2
   */
  lazy val getExchangeRatesLocation2 = sleep1 *> putStrLn("GetExchangeRatesLocation2")

  /**
   * Эффект который все что делает, это спит заданное кол-во времени, в данном случае 1 секунду
   */

  val sleep1 = ZIO.sleep(1 second)


  /**
   * Изменить getExchangeRates, так чтобы они спали по 1 секунде, а затем печатали в консоль
   */


  /**
   * Написать эффект котрый получит курсы из обеих локаций
   */
  lazy val getFrom2Locations = getExchangeRatesLocation1 zip getExchangeRatesLocation2


  /**
   * Написать эффект котрый получит курсы из обеих локаций паралельно
   */
  lazy val getFrom2LocationsInParallel = for{
    fiber1 <- getExchangeRatesLocation1.fork
    fiber2 <- getExchangeRatesLocation2.fork
    r <-  fiber1.join
    r2 <- fiber2.join
  } yield ((r, r2))


  /**
   * Прерывание эффекта
   */

  val rr = for {
    fiber <- getExchangeRatesLocation1.fork
    _ <- getExchangeRatesLocation2
    _ <- fiber.interrupt
    _ <- sleep(3 seconds)
  } yield ()


  val rrr  = for {
    _ <- (sleep(1 seconds) *> putStrLn("Hi")).fork.forever
  } yield ()




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


  // foreachPar и zipPar

  val _ = getExchangeRatesLocation1.zipPar(getExchangeRatesLocation2)

  val r4: ZIO[Console, Nothing, List[Unit]] = ZIO.foreachPar(List(1, 2, 3))(el => putStrLn(el.toString))

  val r5 = getExchangeRatesLocation1 race getExchangeRatesLocation2

  /**
   * Lock
   */


  // Правило 1
  lazy val doSomething: UIO[Unit] = ???
  lazy val doSomethingElse: UIO[Unit] = ???

  lazy val executor: Executor = ???

  lazy val effect = for {
    _ <- doSomething.fork
    _ <- doSomethingElse
  } yield ()

  lazy val result = effect.lock(executor)


  // Правило 2
  lazy val executor1: Executor = ???
  lazy val executor2: Executor = ???

  lazy val effect2 = for {
    _ <- doSomething.lock(executor2).fork
    _ <- doSomethingElse
  } yield ()

  lazy val result2 = effect2.lock(executor1)


  /**
   * простая гонка эффектов
   */
  val res1: URIO[Clock, Int] = ZIO.sleep(3.second).as(4)

  val res2: URIO[Clock, Int] = ZIO.sleep(1.second).as(7)

}