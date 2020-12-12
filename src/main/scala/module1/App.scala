package module1

import module1.list.List

object App {

  def main(args: Array[String]): Unit = {

    val l: List[Int] = List(1, 2, 3, 4)
    val ls: List[String] = List("One", "Two", "Three", "Four")
    println("mkString -> " + l.mkString)
    println("incList -> " + l.incList(l).mkString)
    println("shoutString -> " + ls.shoutString(ls).mkString)
    println("map -> " + ls.map((s:String) => "? + "+s).mkString)
    println("reverse -> " + l.reverse.mkString)

  }
}
