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

  sealed trait Console[+A]
  final case class Println[A](string: String, rest: Console[A]) extends Console[A]
  final case class ReadLine[A](string: String => Console[A]) extends Console[A]
  final case class Return[A](value: () => A) extends Console[A]


  /****
   *  2. Написать интерпретатор для нашей ф-циональной модели
   *
   */

    trait ZIO[-R, +E, +A]

  def interpret[A](console: Console[A]): A = console match {
    case Println(string, rest) =>
      println(string)
      interpret(rest)
    case ReadLine(f) =>
      interpret(f(StdIn.readLine()))
    case Return(value) =>
      value()
  }

  object Console{
    def succeed[A](v: => A): Console[A] = Return(() => v)
    def printLine(str: String): Console[Unit] = Println(str, succeed())
    def readLine: Console[String] = ReadLine(str => succeed(str))
  }

    val p: Console[Unit] = Console.printLine("Hello")



  object consoleOps {

    implicit class ConsoleOps[+A](self: Console[A]){

      def map[B](f: A => B): Console[B] = flatMap(v => Console.succeed(f(v)))

      def flatMap[B](f: A => Console[B]): Console[B] = self match {
        case Println(string, rest) => Println(string, rest.flatMap(f))
        case ReadLine(rest) =>ReadLine(str => rest(str).flatMap(f))
        case Return(value) => f(value())
      }

    }
  }


  import consoleOps._

    val consoleProgram: Console[Unit] = for {
      _ <- Console.printLine("Как тебя зовут?")
      str <- Console.readLine
      _ <- Console.printLine(s"Привет, ${str}")
    } yield ()

    /***
     * Используя consoleOps, получаем более удобный синтаксис построения программы
     */

      lazy val pp = Console.printLine(s"Пока всем")

  lazy val consoleProgram2 = for {
    _ <- consoleProgram
    _ <-  pp
  } yield ()

  lazy val consoleProgram3 = ???

}