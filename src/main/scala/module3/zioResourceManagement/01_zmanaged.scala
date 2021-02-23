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

    def use[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
      acquire.bracket(release)(f)


    def map[B](f: A => B): ZManaged[R, E, B] = ???

    def flatMap[R1 <: R, E1 >: E, B](f: A => ZManaged[R1, E1, B]): ZManaged[R1, E1, B] = ???

    def zip[R1 <: R, E1 >: E, B](that: ZManaged[R1, E1, B]): ZManaged[R1, E1, (A, B)] = ???

  }

  object ZManaged{
    def make[R, E, A](acquire: ZIO[R, E, A], release: A => URIO[R, Any]): ZManaged[R, E, A] = ???
  }

  /**
   * написать эффект открывающий / закрывающий первый файл
   */
  val file1 = ZManaged.make(openFile("test"), closeFile)

  /**
   * написать эффект открывающий / закрывающий второй файл
   */
  val file2 = ZManaged.make(openFile("test2"), closeFile)

  /**
   * Написать эффект, котрый восользуется ф-цией handleFile из блока про bracket для печати строчек в консоль
   */
  val printFile1 = file1.use(handleFile)

  /**
   * Написать комбинированный эффект, который прочитает и выведет строчки из обоих файлов
   */
  val combinedEffect = (file1 zip file2).use{   case (f1, f2) =>
      handleFile(f1) *> handleFile(f2)
  }

}

object zioZManaged{

  lazy val file1: ZManaged[Any, IOException, File] =  ???

  lazy val file2: ZManaged[Any, IOException, File] =  ???

  // Комбинирование
  lazy val combined: ZManaged[Any, IOException, (File, File)] = file1 zip file2
  lazy val combined2: ZManaged[Any, IOException, (File, File)] = file1 zipPar file2

  lazy val fileNames: List[String] = ???

  def file(name: String): ZManaged[Any, IOException, File] = ???

  lazy val files: ZManaged[Any, IOException, List[File]] = ZManaged.foreach(fileNames)(file)

  lazy val files2: ZManaged[Any, IOException, List[File]] = ZManaged.foreachPar(fileNames)(file)


  // Использование

  def processFiles(file: File *): Task[Unit] = ???
  def processFiles2(file: java.io.File *): Task[Unit] = ???

  lazy val r1: ZIO[Any, Throwable, Unit] = files.use(l => processFiles(l :_*))

  lazy val r2: ZIO[Any, Throwable, Unit] = ???

  lazy val files3: ZManaged[Any, IOException, List[java.io.File]] = ???

  /**
   * Прочитать строчки из файла и вернуть список этих строк
   */
lazy val r3: Task[List[String]] = files3.use{ files =>
    Task.foreach(files){ file =>
         Task(Source.fromFile(file).getLines().toList)
    }.map(_.flatten)
  }

  /**
   * Эффект описывающий чтение списка файлов с заданным паралелизмом
   */
  lazy val filesN: ZManaged[Any, Throwable, List[File]] =
    ZManaged.foreachParN(4)(fileNames)(file)


  // Конструирование

  lazy val eff1: Task[Int] = ???

  // Из эффекта
  lazy val m1: ZManaged[Any, Throwable, Int] = ZManaged.fromEffect(eff1)

  // микс ZManaged и ZIO
  lazy val m2: ZManaged[Console, Throwable, Unit] = for {
    _ <- putStrLn("Start processing").toManaged_
    files <- files2
    _ <- processFiles(files :_*).toManaged_
  } yield ()

  //Еще несколько вариантов использования
  val _: ZIO[Console, Throwable, Unit] = m2.useNow

  val _: ZIO[Console, IOException, Unit] = m2.use_(putStrLn("Ooops"))

}