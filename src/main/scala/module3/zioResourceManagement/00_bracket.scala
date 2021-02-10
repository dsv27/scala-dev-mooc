package module3.zioResourceManagement

import java.io.IOException

import module3.zioResourceManagement.tryFinally.traditional.{Resource, use}
import zio.console.{Console, putStrLn}
import zio.duration.durationInt
import zio.{Exit, IO, RIO, Task, UIO, URIO, ZIO, ZManaged}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.language.postfixOps

object tryFinally {

  object traditional{

    trait Resource

    lazy val acquireResource: Resource = ???

    def use(resource: Resource): Unit = ???

    def releaseResource(resource: Resource): Unit = ???

    /**
     * Напишите код, который обеспечит корректную работу с ресурсом:
     * получить ресурс -> использовать -> освободить
     *
     */

    lazy val result1 = {
      val resource = acquireResource
      try {
        use(resource)
      } finally {
        releaseResource(resource)
      }
    }
  }



  object future{
    implicit val global = scala.concurrent.ExecutionContext.global

    lazy val acquireFutureResource: Future[Resource] = ???

    def use(resource: Resource): Future[Unit] = ???

    def releaseResource(resource: Resource): Future[Unit] = ???


    /**
     * Написать вспомогательный оператор, котрый позволит корректно работать
     * с ресурсами в контексте Future
     *
     */
    implicit class FutureOps[+A](future: Future[A]){

      def ensuring(finalizer: Future[Any]): Future[A] = future.transformWith {
        case Failure(exception) => finalizer.flatMap(_ => Future.failed(exception))
        case Success(value) => finalizer.flatMap(_ => Future.successful(value))
      }
    }

    /**
     * Написать код, который получит ресурс, воспользуется им и освободит
     */
     lazy val result2 = acquireFutureResource.flatMap(r => use(r).ensuring(releaseResource(r)))

  }

  object zioBracket{

     trait File{
       def name: String
       def close: Unit = println(s"File -${name}- closed")
       def readLines: List[String] = List("Hello world", "Scala is cool")
     }

     object File{
       def apply(_name: String): File = new File{
         override def name: String = _name
       }

       def apply(_name: String, lines: List[String]): File = new File{
         override def name: String = _name
         override def readLines: List[String] = lines
       }
     }
    /**
     * реалтзовать ф-цию, которая будет описывать открытие файла с помощью ZIO эффекта
     */

     def openFile(fileName: String): IO[IOException, File] = ZIO.fromEither(Right(File("")))

     def openFile(fileName: String, lines: List[String]): IO[IOException, File] =
       ZIO.fromEither(Right(File("test1", lines)))

    /**
     * реалтзовать ф-цию, которая будет описывать закрытие файла с помощью ZIO эффекта
     */

     def closeFile(file: File): UIO[Unit] = URIO(file.close)

    /**
     * Написать эффект, котрый прочитает строчки из файла и выведет их в консоль
     */

      def handleFile(file: File): ZIO[Console, Nothing, List[Unit]] =
        ZIO.foreach(file.readLines)(l => putStrLn(l))




    /**
     * Написать эффект, который откроет 2 файла, прочитает из них строчки,
     * выведет их в консоль и корректно закроет оба файла
     */

     val twoFiles: ZIO[Console, IOException, List[Unit]] =
       ZIO.bracket(openFile("test1"))(closeFile){ f1 =>
        ZIO.bracket(openFile("test2"))(closeFile){ f2 =>
          handleFile(f1) *> handleFile(f2)
        }
     }

    /**
     * Рефакторинг выше написанного кода
     */
     def withFile[R, A](name: String)(use: File => RIO[R, A]): ZIO[R, Throwable, A] =
       openFile(name).bracket(closeFile)(use)


    val twoFiles2: ZIO[Console, Throwable, List[Unit]] =
      withFile("f1"){ f1 =>
        withFile("f2"){ f2 =>
          handleFile(f1) *> handleFile(f2)
        }
      }


  }

}