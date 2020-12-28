package module1

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.util.Timeout
import module1.akka_actors.intro_actors.{Supervisor}


import scala.concurrent.{ExecutionContext}
import scala.concurrent.duration._

object App {


  def main(args: Array[String]): Unit = {
    // Create ActorSystem and top level supervisor
    implicit val system = ActorSystem[SpawnProtocol.Command](Supervisor(), "user-guardian")
    implicit val ec: ExecutionContext = system.executionContext
    implicit val timeout: Timeout = Timeout(3 seconds)



    Thread.sleep(3000)
    system.terminate()
  }
}
