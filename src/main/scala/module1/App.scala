package module1

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.util.Timeout
import module1.akka_actors.intro_actors.actors_communication.AccountOpsSupervisor.{AccountOpsProtocol, GetBalance, Withdraw}
import module1.akka_actors.intro_actors.change_behaviour.Worker.WorkerProtocol
import module1.akka_actors.intro_actors.handle_state.Counter.CounterProtocol
import module1.akka_actors.intro_actors.{Supervisor, abstract_behaviour, actors_communication, behaviours_factory_methods, change_behaviour, handle_state}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object App {


  def main(args: Array[String]): Unit = {
    // Create ActorSystem and top level supervisor
    implicit val system = ActorSystem[SpawnProtocol.Command](Supervisor(), "user-guardian")
    implicit val ec: ExecutionContext = system.executionContext
    implicit val timeout: Timeout = Timeout(3 seconds)

    val actorRef: Future[ActorRef[AccountOpsProtocol]] =
      system.ask(SpawnProtocol.Spawn(actors_communication.AccountOpsSupervisor(), "account-supervisor", Props.empty, _))

    for(ref <- actorRef){
      ref ! GetBalance("1")
      ref ! Withdraw("1", 500)
      ref ! GetBalance("1")
    }

    Thread.sleep(3000)
    system.terminate()
  }
}
