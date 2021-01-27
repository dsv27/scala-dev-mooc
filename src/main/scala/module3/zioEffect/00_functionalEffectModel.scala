package module3.zioEffect

import scala.io.StdIn


/****
  *
  * Функциоанльный эффект - структура данных, шаблон описывающий вычисление
  *
  */

  object simpleProgram {
    /****
     *  1. Простая консольная программа, которая спросит как тебя зовут, а потом напечатает ответ
     *  Привет, <твое_имя>
     */

      println("Как тебя  зовут?")
      val response = StdIn.readLine()
      println(s"Привет, ${response}")

  }


  object functionalProgram {

  /****
   *  2. Написать функциональную модель, которая позволит описывать программы читающие и печатающие строки в консоль
   *
   */

  type Console[+A]
  type Println[A]
  type ReadLine[A]
  type Return[A]


  /****
   *  2. Написать интерпретатор для нашей ф-циональной модели
   *
   */


  def interpret[A](console: Console[A]): A = ???

  object Console{
    def succeed[A](v: => A): Console[A] = ???
    def printLine(str: String): Console[Unit] = ???
    def readLine: Console[String] = ???
  }


  val consoleProgram = ???





  object consoleOps {

    implicit class ConsoleOps[+A](self: Console[A]){

      def map[B](f: A => B): Console[B] = ???

      def flatMap[B](f: A => Console[B]): Console[B] = ???

    }
  }




  import consoleOps._

    /***
     * Используя consoleOps, получаем более удобный синтаксис построения программы
     */

  val consoleProgram2 = ???

  val consoleProgram3 = ???

}