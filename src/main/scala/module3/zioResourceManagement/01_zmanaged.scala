package module3.zioResourceManagement

import java.io.IOException

import module3.zioResourceManagement.tryFinally.zioBracket
import module3.zioResourceManagement.tryFinally.zioBracket.{File, handleFile}
import zio.{Exit, Reservation, Task, URIO, ZIO, ZManaged}
import zio.console.{Console, putStrLn}

import scala.io.Source

object toyZManaged{

  import  zioBracket._

  final case class ZManaged[-R, +E, A](
                                        acquire: ZIO[R, E, A],
                                        release: A => URIO[R, Any]
                                      ){ self =>
    def use[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] = ???


    def map[B](f: A => B): ZManaged[R, E, B] = ???

    def flatMap[R1 <: R, E1 >: E, B](f: A => ZManaged[R1, E1, B]): ZManaged[R1, E1, B] = ???

    def zip[R1 <: R, E1 >: E, B](that: ZManaged[R1, E1, B]): ZManaged[R1, E1, (A, B)] = ???

  }

  object ZManaged{
    def make[R, E, A](acquire: ZIO[R, E, A], release: A => URIO[R, Any]): ZManaged[R, E, A] = ???
  }

  /**
   * написать эффект открывающий первый файл
   */
  val file1 = ???

  /**
   * написать эффект открывающий второй файл
   */
  val file2 = ???

  /**
   * Написать эффект, котрый восользуется ф-цией handleFile для печати строчек в консоль
   */
  val printFile1 = ???

  /**
   * Написать комбинированный эффект, который прочитает и выведет строчки из обоих файлов
   */
  val combinedEffect = ???

}

object zioZManaged{

  lazy val file1: ZManaged[Any, IOException, File] =  ???
  lazy val file2: ZManaged[Any, IOException, File] =  ???

  // Комбинирование
  lazy val combined: ZManaged[Any, IOException, (File, File)] = ???
  lazy val combined2: ZManaged[Any, IOException, (File, File)] = ???

  lazy val fileNames: List[String] = ???

  def file(name: String): ZManaged[Any, IOException, File] = ???

  lazy val files: ZManaged[Any, IOException, List[File]] = ???
  lazy val files2: ZManaged[Any, IOException, List[File]] = ???


  // Использование

  def processFiles(file: File *): Task[Unit] = ???
  def processFiles2(file: java.io.File *): Task[Unit] = ???

  lazy val r1: ZIO[Any, Throwable, Unit] = ???

  lazy val r2: ZIO[Any, Throwable, Unit] = ???

  lazy val files3: ZManaged[Any, IOException, List[java.io.File]] = ???

  /**
   * Прочитать строчки из файла и вернуть список этих строк
   */
  lazy val r3: Task[List[String]] = ???

  /**
   * Эффект описывающий чтение списка файлов с заданным паралелизмом
   */
  lazy val filesN: ZManaged[Any, Throwable, List[File]] = ???


  // Конструирование

  lazy val eff1: Task[Int] = ???

  // Из эффекта
  lazy val m1: ZManaged[Any, Throwable, Int] = ???

  // микс ZManaged и ZIO
  lazy val m2 = ???

  //Еще несколько вариантов использования
  val _: ZIO[Console, Throwable, Unit] = ???

  val _: ZIO[Console, IOException, Unit] = ???

}