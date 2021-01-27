package module3.zioEffect

import java.io.IOException

import zio.{Fiber, IO, Ref, URIO, ZIO}
import zio.clock.Clock
import zio.console.{Console, getStrLn, putStrLn}
import zio.duration._

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


  def getFromService(ref: Ref[Int]) = for {
    count <- ref.getAndUpdate(_ + 1)
    _ <- putStrLn(s"GetFromService - ${count}") *> ZIO.sleep(1 seconds)
  } yield ()

  def sendToDB(ref: Ref[Int]): ZIO[Clock with Console, Exception, Unit] = for {
    count <- ref.getAndUpdate(_ + 1)
    _ <- ZIO.sleep(5 seconds) *> putStrLn(s"SendToDB - ${count}") *> ZIO.sleep(1 seconds)
  } yield ()


  lazy val app1 = ???

  val res1: URIO[Clock, Int] = ZIO.sleep(3.second).as(4)

  val res2: URIO[Clock, Int] = ZIO.sleep(1.second).as(7)

}