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

    lazy val result1 = ???
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
      def ensuring(finalizer: Future[Any]): Future[A] = ???
    }

    /**
     * Написать код, который получит ресурс, воспользуется им и освободит
     */
     lazy val result2 = ???

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

     def openFile(fileName: String): IO[IOException, File] = ???
     def openFile(fileName: String, lines: List[String]): IO[IOException, File] = ???

    /**
     * реалтзовать ф-цию, которая будет описывать закрытие файла с помощью ZIO эффекта
     */

     def closeFile(file: File): UIO[Unit] = ???

    /**
     * Написать эффект, котрый прочитает строчки из файла и выведет их в консоль
     */

      def handleFile(file: File) = ???




    /**
     * Написать эффект, который откроет 2 файла, прочитает из них строчки, выведет их в консоль и корректно закроет оба файла
     */

     val twoFiles = ???

    /**
     * Рефакторинг выше написанного кода
     */



  }

}