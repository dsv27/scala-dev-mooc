package module3.zioResourceManagement

import java.io.IOException

import module3.zioResourceManagement.tryFinally.zioBracket
import module3.zioResourceManagement.tryFinally.zioBracket.{File, handleFile}
import zio.{URIO, ZIO, ZManaged}
import zio.console.Console

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
  // Создание
  import zioBracket._
  lazy val file1: ZManaged[Any, IOException, File] =  ???
  lazy val file2: ZManaged[Any, IOException, File] =  ???

  // Комбинирование
  lazy val combined: ZIO[Console, IOException, List[Unit]] = ???


  // Использование


}