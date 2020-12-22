package module1

import module1.list.List.Cons

import scala.annotation.tailrec
import module1.list.List.Nil
import module1.opt.Option.None

/**  Реализуем тип Option
  */

object opt {

  /** Реализовать тип Option, который будет указывать на присутствие либо отсутсвие результата
    */

  sealed trait Option[+A] {

    /** Реализовать метод isEmpty, который будет возвращать true если Option не пуст и false в противном случае
      */
    def isEmpty: Boolean = this match {
      case Option.Some(_) => true
      case Option.None    => false
    }

    /** Реализовать метод get, который будет возвращать значение
      */

    def get: A = this match {
      case Option.Some(v) => v
      case Option.None    => throw new Exception("get on empty Option")
    }

    /** Реализовать метод printIfAny, который будет печатать значение, если оно есть
      */
    def printIfAny: Unit = this match {
      case Option.Some(v) => println(v)
      case Option.None    => Option.None
    }

    /** реализовать метод orElse который будет возвращать другой Option, если данный пустой
      */
    def orElse[B >: A](o: => Option[B]): Option[B] = this match {
      case Option.Some(v) => this
      case Option.None    => o
    }

    /** Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
      */
    def zip[B >: A](o: Option[B]): Option[(A, B)] = {

      (this, o) match {
        case (Option.Some(a), Option.Some(b)) => Option.Some((a, b))
        case _                                => None
      }

    }

    /** Реализовать метод filter, который будет возвращать не пустой Option
      * в случае если исходный не пуст и предикат от значения = true
      */
    def filter(p: A => Boolean): Option[A] = this match {
      case Option.Some(v) if p(v) => this
      case _                      => Option.None
    }
  }

  object Option {
    case class Some[A](v: A) extends Option[A]
    case object None extends Option[Nothing]
  }

}

object recursion {

  /** Реализовать метод вычисления n!
    * n! = 1 * 2 * ... n
    */

  def fact(n: Int): Long = {
    var _n = 1L
    var i = 2
    while (i <= n) {
      _n *= i
      i += 1
    }
    _n
  }

  def !!(n: Int): Long = {
    if (n <= 1) 1
    else n * !!(n - 1)
  }

  def !(n: Int): Long = {
    @tailrec
    def loop(n1: Int, acc: Long): Long = {
      if (n <= 1) acc
      else loop(n1 - 1, n1 * acc)
    }
    loop(n, 1)
  }

}

object list {

  /** Реализовать односвязанный имутабельный список List
    */

  sealed trait List[+A] {

    def isEmpty: Boolean = this eq List.Nil
    def head: A
    def tail: List[A]

    /** Реализовать метод конс :: который позволит добавлять элемент в голову списка
      */
    def ::[B >: A](head: B): List[B] = Cons(head, this)

    /** Реализовать метод mkString который позволит красиво представить список в виде строки
      */
    def mkString: String = mkString(", ")

    def mkString(sep: String): String = {
      import List._

      def loop(l: List[A], acc: StringBuilder): StringBuilder = {

        l match {
          case List.Nil => acc
          case h :: Nil => acc.append(s"$h")
          case h :: t   => loop(t, acc.append(s"$h$sep"))
        }
      }
      loop(this.reverse, new StringBuilder()).toString()
    }

    /** Количество элементов в List[A]
      */

    def length: Int = {
      var t: List[A] = this
      var l: Int = 0
      while (!t.isEmpty) {
        l += 1
        t = t.tail
      }
      l
    }

    /** Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
      */
    def reverse: List[A] = {
      import List._
      var r: List[A] = List.Nil

      def loop(l: List[A], acc: List[A]): List[A] = {
        l match {
          case List.Nil => acc
          case h :: Nil => l.head :: acc
          case h :: t   => loop(l.tail, l.head :: acc)
        }
      }
      loop(this, r)
    }

    /** Написать функцию incList котрая будет принимать список Int и возвращать список,
      * где каждый элемент будет увеличен на 1
      */

    def incList(l: List[Int]): List[Int] = l.map(_ + 1)

    /** Написать функцию shoutString котрая будет принимать список String и возвращать список,
      * где к каждому элементу будет добавлен префикс в виде '!'
      */
    def shoutString(l: List[String]): List[String] = l.map("!" + _)

    /** Реализовать метод для списка который будет применять некую ф-цию к элементам данного списка
      */
    def map[B](f: A => B): List[B] = {
      import List._
      var a: List[B] = List.Nil

      def loop(l: List[A], acc: List[B], f: A => B): List[B] = {
        l match {
          case List.Nil => acc
          case h :: Nil => f(l.head) :: acc
          case h :: t   => loop(l.tail, f(l.head) :: acc, f)
        }
      }
      loop(this.reverse, a, f)

    }
    /*if (this eq List.Nil) List.Nil
      else {
        val h: B = f(head)
        var t: List[B] = List(h)
        var rest = tail
        while (rest ne List.Nil) {
          val nx: B = f(rest.head)
          t = nx :: t
          rest = rest.tail
        }
        t
      }*/

  }

  object List {

    case object Nil extends List[Nothing] {
      override def head: Nothing = throw new NoSuchElementException(
        "head of empty list"
      )
      override def tail: Nothing = throw new UnsupportedOperationException(
        "tail of empty list"
      )
    }

    /** Реализовать метод конс :: который позволит добавлять элемент в голову списка
      */
    case class ::[A](hd: A, tl: List[A]) extends List[A] {
      override def head: A = hd
      override def tail: List[A] = tl
    }
    val Cons = ::

    /** Реализовать конструктор, для создания списка n элементов
      */
    def apply[T](arg: T*): List[T] = {
      var l: List[T] = List.Nil
      arg.foreach(el => l = el :: l)
      l
    }
  }

  val list = 1 :: List.Nil

}
