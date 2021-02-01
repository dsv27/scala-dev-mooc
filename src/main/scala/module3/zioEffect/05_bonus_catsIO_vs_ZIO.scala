package module3.zioEffect

import java.util.concurrent.{Executors, ThreadFactory}

import cats.effect

import scala.concurrent.ExecutionContext

object catsIOvsZIO {

  /**
   *
   * Несмотря на то, что ZIO и cats.IO очень похоже, есть принципиальные отличия,
   * в том числе и при возникновении Concurrency
   * Здесь рассмотрим эти ньюансы
   */
  class NamedThreadFactory(name: String) extends ThreadFactory {
    override def newThread(r: Runnable): Thread = new Thread(r, name)
  }

  val ec1 = ExecutionContext.fromExecutor(Executors.newCachedThreadPool(
    new NamedThreadFactory("ec1")))
  val ec2 = ExecutionContext.fromExecutor(Executors.newCachedThreadPool(
    new NamedThreadFactory("ec2")))
  val ec3 = Executors.newCachedThreadPool(new NamedThreadFactory("ec3"))

  val cs1 = cats.effect.IO.contextShift(ec1)
  val cs2 = cats.effect.IO.contextShift(ec2)

  val printThread = cats.effect.IO {
    println(Thread.currentThread().getName)
  }

  def run(name: String)(th: cats.effect.IO[_]): Unit = {
    println(s"-- $name --")
    th.unsafeRunSync()
    println()
  }

}