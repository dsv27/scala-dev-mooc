package module1

object subtyping {

  /** Наследование - это отношение межу типами.
    * Отношение вида подтип / супертип
    * Компилятор способен отслеживать это отношение и следить за его соблюдением
    * В Scala мы имеем возможность указать компилятору на наличие отношения между Generic параметрами
    *
    * Для этого используется комбинация специальных символов(type operator)
    *  <: -   для отношения `подтип`
    *  >: -   для отношения `супертип`
    */

  trait Vehicle
  trait Car extends Vehicle
  trait Moto extends Vehicle
  object Harley extends Moto
  object Mustang extends Car

  type IsSubtypeOf[A, B >: A]
  type IsSupertypeOf[A, B <: A]

  /** С помощью типа IsSubtypeOf выразить отношение Car и Vehicle
    */
 // val t1: IsSubtypeOf[Car, Vehicle] == ???
 // val t2: IsSubtypeOf[Vehicle, Car] == ??? // Неверно

  /** С помощью типа IsSubtypeOf выразить отношение Car и Mustang
    */
  //val t3: IsSupertypeOf[Car, Mustang.type]

  /** С помощью типа выразить отношение Vehicle и Harley, причем чтобы они шли в этом порядке
    */
  //val t4: IsSupertypeOf[Vehicle, Harley.type]

  /** В этом примере вам нужно правильно выбрать оператор отношения,
    * чтобы среди идущих ниже выражений, те которые корректны по смыслу компилировались, а остальные нет
    */

  def isInstanceOf[A, B >: A](a: A): Unit = ???

  lazy val mustCompile1 = isInstanceOf[Mustang.type, Car](Mustang)
  lazy val mustCompile2 = isInstanceOf[Harley.type, Moto](Harley)
  //lazy val mustNotCompile1 = isInstanceOf[Mustang.type, Moto](Mustang)
  //lazy val mustNotCompile2 = isInstanceOf[Harley.type, Car](Harley)

//Инвариантен
  trait Box[T] {
    def get: T
  }
  //Ковариантен
  trait Box1[+T] {
    def get: T
  }
  // val a : IsSubtypeOf[Box[Car], Box[Vehicle]] = ???  // Падает
  val a: IsSubtypeOf[Box1[Car], Box1[Vehicle]] = ??? // Работает

  trait Consumer[T] {
    def consume(v: T): Unit
  }
//Контрвариант
  trait Consumer1[-T] {
    def consume(v: T): Unit
  }
  //val b : IsSubtypeOf[Consumer[Car], Consumer[Vehicle]] = ??? // Нескомпилируется
  //val b: IsSubtypeOf[Consumer1[Car], Consumer1[Vehicle]] = ???

  trait Box2[+T] {
    def get: T
    def put[TT >: T](v: TT): Unit
  }
  trait Consumer2[-T] {
    def consume(v: T): Unit
    def produce[TT <: T](): TT
  }

  object adt {

    object tuples {

      /** Products
        * Произведение типов A * B - это такой тип,
        * который позволит закодировать все возможные комбинации значений типов А и В
        */

      // Unit * Boolean -- Unit ->(), Boolean --> true, false
      // Возможные комбинации
      // () true
      // () false

      // Int * Boolean Int 2**32, Boolean --> true, false

      /** Tuples
        * Наиболее общий способ хранить 2 и более кусочка информации в одно время. По русски - кортеж.
        * Вместе с кортежем мы получаем из коробки конструктор / деконсруктор, сравнение, hashCode, copy,
        * красивое строковое представление
        */
      type ProductUnitBoolean = (Unit, Boolean)
      val v1: ProductUnitBoolean = ((), true)
      val v2: ProductUnitBoolean = ((), false)

      /** Реализовать тип Person который будет содержать имя и возраст
        */

      type Person = (String, Int)

      /**  Реализовать тип `CreditCard` который может содержать номер (String),
        *  дату окончания (java.time.YearMonth), имя (String), код безопастности (Short)
        */
      type CreditCard = (String, java.time.YearMonth, String, Short)
    }

    object case_classes {

      /** Case classes
        */

      final case class Person(name: String, age: Int)

      val tonyStark: Person = Person("Tony Stark", 42)

      /** используя паттерн матчинг напечатать имя и возраст
        */

      def printNameAndAge: Unit = tonyStark match {
        case Person(n, a) => println(s"$n, $a")
      }

      final case class CreditCard(
          number: String,
          expDate: java.time.YearMonth,
          name: String,
          CVC: Short
      )
      final case class Employee(name: String, address: Address)
      final case class Address(street: String, number: Int)

      val alex = Employee("Alex", Address("XXX", 221))

      /** воспользовавшись паттерн матчингом напечатать номер из поля адрес
        */
      def printEm: Unit = alex match {
        case Employee(_, Address(_, n)) => println(s"$n")
      }
      // alex match

      /** Паттерн матчинг может содержать литералы.
        * Реализовать паттерн матчинг на alex с двумя кейсами.
        * 1. Имя должно соотвествовать Alex
        * 2. Все остальные
        */
      alex match {
        case Employee("Alex", _) => println("Alex")
        case _                   => println("Other")
      }

      /** Паттерны могут содержать условия. В этом случае case сработает,
        * если и паттерн совпал и условие true.
        * Условия в паттерн матчинге называются гардами.
        */

      /** Реализовать паттерн матчинг на alex с двумя кейсами.
        * 1. Имя должно начинаться с A
        * 2. Все остальные
        */
      alex match {
        case Employee(name, _) if name.startsWith("A") => print(s"$name")
        case _                                         => println("Other")
      }

      /** Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
        * x @ ..., где x это любая переменная. Это переменная может использоваться, как в условии,
        * так и внутри кейса
        */
      alex match {
        case e @ Employee(_, _) if e.name.startsWith("A") => print(s"$e.name")
        case _                                            => println("Other")
      }

      /** Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
        */
      val x: Int = ???
      x match {
        case 1 | 2 | 3 => ???
        case _         => ???
      }
    }

    object either {

      /** Sum
        * Сумма типов A и B - это такой тип,
        * который позволит закодировать все значения типа A и все значения типа B
        */

      // Unit + Boolean

      /** Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
        * Также как и кортежи обладает целым рядом полезных методов
        * Иммутабелен
        */

      type IntOrString = Either[Int, String]

      /** Реализовать экземпляр типа IntOrString с помощью конструктора Right(_)
        */
      //val intOrString: IntOrString = Right("")
      val intOrString: IntOrString = Left(0)

      /** \
        * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
        */
      type PaymentMethod = Either[CreditCard, WireTransfer]
      type PaymentMethod3 = Either[CreditCard, Either[WireTransfer, Cash]]
      final case class CreditCard()
      final case class WireTransfer()
      final case class Cash()

    }

    object sealed_traits {

      /** Также Sum type можно представить в виде sealed trait с набором альтернатив
        */

      sealed trait Card
      object Card {
        final case class Clubs(points: Int) extends Card // крести
        final case class Diamonds(points: Int) extends Card // бубны
        final case class Spades(points: Int) extends Card // пики
        final case class Hearts(points: Int) extends Card // червы
      }

      lazy val card: Card = Card.Spades(10)

      /** Написать паттерн матчинг на 10 пику, и на все остальное
        */
      //card match {
      //  case Spades(10) => println("пики 10")
      //case _          => print("Other")
      //}

      /** Написать паттерн матчинг который матчит карты номиналом >= 10
        */

    }

  }

  object type_classes {

    /** Type class - это паттерн родом из Haskel
      * Он позволяет расширять существующие типы новым функционалом,
      * без необходимости менять их исходники или использовать наследование
      *
      * Компоненты паттерна:
      * 1. Сам type class
      * 2. Его экземпляры
      * 3. Методы которые его используют
      *
      * Необходимые Scala конструкции
      *  trait
      *  implicit values
      *  implicit parameters
      *  implicit class
      */

    sealed trait JsValue
    object JsValue {
      final case class JsObject(get: Map[String, JsValue]) extends JsValue
      final case class JsString(get: String) extends JsValue
      final case class JsNumber(get: Double) extends JsValue
      final case class JsInteger(get: Int) extends JsValue
      final case object JsNull extends JsValue
    }

    // type_class ниже
    trait JsonWriter[T] {
      def write(v: T): JsValue
    }
    object JsonInstances {
      implicit val strJson = new JsonWriter[String] {
        override def write(v: String): JsValue = JsValue.JsString(v)
      }
      implicit val intJson = new JsonWriter[Int] {
        override def write(v: Int): JsValue = JsValue.JsInteger(v)//JsString(v)
      }
    }
    object JsonSyntax{
      implicit  class jsonOps[A](v: A){
      def toJson(implicit JsonWriter:JsonWriter[A]): JsValue = JsonWriter.write(v)
      }
    }
    object Json {
      def toJson[T](v: T)(implicit JsonWriter: JsonWriter[T]): JsValue =
        JsonWriter.write(v)
    }
    import JsonInstances._
    import JsonSyntax._

    val jsString: JsValue = Json.toJson("Hello")
    //val jsString: JsValue = Json.toJson(21)

    /** в Scala есть специальный метод позволяющий получить инстанс type класса из контекста
      */

    // implicitly

    /** Упаковка имплиситов
      * Имплисты могут располагаться либо внутри объектов / классов / трэйтов
      *
      * Имплиситы помещенные в объект компаньон для типа,
      * автоматически попадают в скоуп, где мы используем данный тип
      */

    /** Поиск имплиситов
      *
      *  - локальные либо наследованные
      *  - импортированные
      *  - объект компаньон
      */

  }

}
