package module3.zioEffect

import java.io.IOException

import scala.io.StdIn
import zio._
import zio.blocking._


object zioBlocking{


  val _: RIO[Blocking, String] = effectBlocking(StdIn.readLine())

  val _: ZIO[Blocking, IOException, String] = effectBlocking(StdIn.readLine()).refineToOrDie[IOException]

  val _: ZIO[Blocking, IOException, String] = effectBlockingIO(StdIn.readLine())

  val _: ZIO[Blocking, IOException, String] = effectBlockingCancelable(StdIn.readLine())(ZIO.unit)

  val _: RIO[Blocking, Unit] = effectBlockingInterrupt(Thread.sleep(10000))



}