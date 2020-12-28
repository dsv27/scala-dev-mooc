package module1.akka_actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, MessageAdaptionFailure, PostStop, PreRestart, SpawnProtocol, SupervisorStrategy}
import module1.akka_actors.intro_actors.AccountHandler.AccountResponseProtocol
import module1.akka_actors.intro_actors.actors_communication.AccountDispatcher.AccountDispatcherResponseProtocol
import module1.akka_actors.intro_actors.actors_communication.AccountNumber
import module1.akka_actors.intro_actors.change_behaviour.Worker.WorkerProtocol.{StandBy, Start, Stop}
import module1.akka_actors.intro_actors.handle_state.Counter.CounterProtocol.{Inc}



object intro_actors{


  /**
   * Два основных компонента актора:
   * 1. Behaviour
   * 2. Context
   *
   * Для создания можно воспользоваться фабричными методами объекта Behaviours или заэкстендить AbstractBehavior
   */

  object Supervisor {
    def apply(): Behavior[SpawnProtocol.Command] = Behaviors.setup{ ctx =>
      ctx.log.info(ctx.self.toString)
      SpawnProtocol()
    }
  }



  object behaviours_factory_methods {
    object Echo{
      def apply(): Behavior[String] = ???
    }
  }


  object abstract_behaviour {
    object Echo {
      def apply(): Behavior[String] = ???
    }

  }


  /**
   * Реализовать актор, который будет менять свое поведение в ответ на сообщения
   */

  object change_behaviour {



    object Worker{
      sealed trait WorkerProtocol
      object WorkerProtocol{
        case object Start extends WorkerProtocol
        case object StandBy extends WorkerProtocol
        case object Stop extends WorkerProtocol
      }

      def apply(): Behavior[WorkerProtocol] = idle()

      def idle(): Behavior[WorkerProtocol] = ???
      def workInProgress(): Behavior[WorkerProtocol] = ???
      }
    }




  /***
   * 1. Реализовать актор который будет считать полученные им сообщения
   * 2. Доработать актор так, чтобы он мог возвращать текущий Counter
   */

  object handle_state {
    object Counter{

      sealed trait CounterProtocol
      object CounterProtocol{
        final case object Inc extends CounterProtocol
      }

      def apply(init: Int): Behavior[CounterProtocol] = ???

      def inc: Behavior[CounterProtocol] = ???
    }
  }

  /**
   * Что нужно сделать, если мы захотим увеличивать счетчик не на 1, а на любое число
   */


  /**
   * Взаимодействие между акторами, адаптер паттерн
   */
  object actors_communication{
    type AccountNumber = String

    object AccountOpsSupervisor {

      sealed trait AccountOpsProtocol
      final case class Withdraw(from: AccountNumber, amount: Long) extends AccountOpsProtocol
      final case class GetBalance(of: AccountNumber) extends AccountOpsProtocol
      final case class Transfer(from: AccountNumber, to: AccountNumber, amount: Long) extends AccountOpsProtocol
      final case class AccountManagerProtocolWrapper(msg: AccountDispatcherResponseProtocol) extends AccountOpsProtocol

      def apply(): Behavior[AccountOpsProtocol] = ???


    }

    private[akka_actors] object AccountDispatcher {
      sealed trait AccountDispatcherRequestProtocol
      final case class Withdraw(from: AccountNumber, amount: Long, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol
      final case class GetBalance(of: AccountNumber, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol
      final case class Transfer(from: AccountNumber, to: AccountNumber, amount: Long, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol
      final case class AccountResponseWrapper(msg: AccountResponseProtocol, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol


      sealed trait AccountDispatcherResponseProtocol
      final case class Balance(of: AccountNumber, amount: Long) extends AccountDispatcherResponseProtocol


      def apply(): Behavior[AccountDispatcherRequestProtocol] = ???
    }
  }

  private[akka_actors] object AccountHandler{
    sealed trait AccountProtocol
    final case class Withdraw(from: AccountNumber, amount: Long, replyTo: ActorRef[AccountResponseProtocol]) extends AccountProtocol
    final case class GetBalance(of: AccountNumber, replyTo: ActorRef[AccountResponseProtocol]) extends AccountProtocol
    final case class Transfer(from: AccountNumber, to: AccountNumber, amount: Long, replyTo: ActorRef[AccountResponseProtocol]) extends AccountProtocol

    sealed trait AccountResponseProtocol
    case class OperationSuccess(balance: Long, from: AccountNumber) extends AccountResponseProtocol
    case class OperationFailure(balance: Long, from: AccountNumber) extends AccountResponseProtocol



    def apply(initBalance: Long): Behavior[AccountProtocol] = ???

    def default(balance: Long): Behavior[AccountProtocol] = ???
  }


}