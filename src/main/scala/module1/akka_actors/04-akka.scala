package module1.akka_actors

import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, MessageAdaptionFailure, PostStop, PreRestart, SpawnProtocol, SupervisorStrategy}
import module1.akka_actors.intro_actors.AccountHandler.AccountResponseProtocol
import module1.akka_actors.intro_actors.actors_communication.AccountDispatcher.AccountDispatcherResponseProtocol
import module1.akka_actors.intro_actors.actors_communication.AccountNumber
import module1.akka_actors.intro_actors.change_behaviour.Worker.WorkerProtocol.{StandBy, Start, Stop}
import module1.akka_actors.intro_actors.handle_state.Counter.CounterProtocol.{GetCounter, Inc}



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
      def apply(): Behavior[String] = Behaviors.setup{ctx =>
        Behaviors.receiveMessage{
          case msg =>
            ctx.log.info(msg)
            Behaviors.same
        }
      }
    }
  }


  object abstract_behaviour {
    object Echo {

      def apply(): Behavior[String] = Behaviors.setup{ctx =>
        new Echo(ctx)
      }

      class Echo(ctx: ActorContext[String]) extends AbstractBehavior[String](ctx){
        override def onMessage(msg: String): Behavior[String] = {
          ctx.log.info(msg)
          this
        }
      }
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

      def idle(): Behavior[WorkerProtocol] = Behaviors.setup{ctx =>
        Behaviors.receiveMessage{
          case msg @ Start =>
            ctx.log.info(s"$msg")
            workInProgress()
          case msg @ StandBy =>
            ctx.log.info(s"$msg")
            idle()
          case Stop =>
            ctx.log.info("Остановка")
            Behaviors.stopped
        }
      }
      def workInProgress(): Behavior[WorkerProtocol] = Behaviors.setup{ctx =>
          Behaviors.receiveMessage{
            case msg @ Start => Behaviors.unhandled
            case msg @ StandBy =>
              ctx.log.info(s"Заканчиваю работу")
              idle()
            case Stop =>
              ctx.log.info("Остановка")
              Behaviors.stopped
          }
        }
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
        final case class GetCounter(replyTo: ActorRef[Int]) extends CounterProtocol
      }

      def apply(init: Int): Behavior[CounterProtocol] = inc(init)

      def inc(counter: Int): Behavior[CounterProtocol] = Behaviors.setup{ctx =>
        Behaviors.receiveMessage{
          case Inc =>
            ctx.log.info(s"Inc $counter")
            inc(counter + 1)
          case GetCounter(replyTo) =>
            replyTo ! counter
            Behaviors.same
        }
      }
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

      def apply(): Behavior[AccountOpsProtocol] = Behaviors.setup{ ctx =>

        val accountManager: ActorRef[AccountDispatcher.AccountDispatcherRequestProtocol] =
          ctx.spawn(AccountDispatcher(), "account-manager")

        val adapter: ActorRef[AccountDispatcherResponseProtocol] =
          ctx.messageAdapter[AccountDispatcherResponseProtocol](msg => AccountManagerProtocolWrapper(msg))

        Behaviors.receiveMessage {
          case Withdraw(from, amount) =>
            ctx.log.info(s"Withdraw $from $amount")
            accountManager ! AccountDispatcher.Withdraw(from, amount, adapter)
            Behaviors.same
          case GetBalance(of) =>
            ctx.log.info(s"GetBalance $of")
            accountManager ! AccountDispatcher.GetBalance(of, adapter)
            Behaviors.same
          case Transfer(from, to, amount) =>
            ctx.log.info(s"Transfer $from $to $amount")
            accountManager ! AccountDispatcher.Transfer(from, to, amount, adapter)
            Behaviors.same
          case AccountManagerProtocolWrapper(b @ AccountDispatcher.Balance(of, amount)) =>
            ctx.log.info(s"AccountOpsSupervisor $b")
            Behaviors.same
        }
      }


    }

    private[akka_actors] object AccountDispatcher {
      sealed trait AccountDispatcherRequestProtocol
      final case class Withdraw(from: AccountNumber, amount: Long, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol
      final case class GetBalance(of: AccountNumber, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol
      final case class Transfer(from: AccountNumber, to: AccountNumber, amount: Long, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol
      final case class AccountResponseWrapper(msg: AccountResponseProtocol, replyTo: ActorRef[AccountDispatcherResponseProtocol]) extends AccountDispatcherRequestProtocol


      sealed trait AccountDispatcherResponseProtocol
      final case class Balance(of: AccountNumber, amount: Long) extends AccountDispatcherResponseProtocol


      def apply(): Behavior[AccountDispatcherRequestProtocol] = Behaviors.setup{ ctx =>
        val account1 = ctx.spawn(AccountHandler(10000L), "account-1")

        // Создаем адаптер для обработки ответов от Account
        def accountAdapter(replyTo: ActorRef[AccountDispatcherResponseProtocol]) =
          ctx.messageAdapter[AccountResponseProtocol](AccountResponseWrapper(_, replyTo))

        Behaviors.receiveMessage {
          case w@Withdraw(from, amount, replyTo) =>
            ctx.log.info(s"AccountDispatcher - $w")
            account1 ! AccountHandler.Withdraw(from, amount, accountAdapter(replyTo))
            Behaviors.same
          case t@Transfer(from, to, amount, replyTo) =>
            ctx.log.info(s"AccountDispatcher - $t")
            Behaviors.same
          case g@GetBalance(of, replyTo) =>
            ctx.log.info(s"AccountDispatcher - $g")
            account1 ! AccountHandler.GetBalance(of, accountAdapter(replyTo))
            Behaviors.same
          case AccountResponseWrapper(msg, replyTo) => msg match {
            case s@AccountHandler.OperationSuccess(balance, from) =>
              ctx.log.info(s"AccountDispatcher - $s")
              replyTo ! Balance(from, balance)
              Behaviors.same
            case f@AccountHandler.OperationFailure(balance, from) =>
              ctx.log.info(s"AccountDispatcher - $f")
              replyTo ! Balance(from, balance)
              Behaviors.same
          }
        }
      }
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



    def apply(initBalance: Long): Behavior[AccountProtocol] = default(initBalance)

    def default(balance: Long): Behavior[AccountProtocol] = Behaviors.setup{ ctx =>

      Behaviors.receiveMessage{
        case Withdraw(from, amount, replyTo) =>
          val newBalance = balance - amount
          replyTo ! OperationSuccess(newBalance, from)
          default(newBalance)
        case GetBalance(of, replyTo) =>
          replyTo ! OperationSuccess(balance, of)
          default(balance)
        case Transfer(from, to, amount, replyTo) =>
          default(balance)
      }
    }
  }


}