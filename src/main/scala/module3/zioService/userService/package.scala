package module3.zioService



import module3.zioService.dbService.DbService
import zio.console.Console
import zio.{ERef, Has, IO, Ref, Task, UIO, ZIO, ZLayer, ZRef, console}

package object userService {

  case class UserAlreadyExist(msg: String) extends Throwable(msg)

  type UserService = Has[UserService.Service]

  object UserService{

    trait Service{
      def putUser(user: User): IO[UserAlreadyExist, User]
      def findUser(id: Int): UIO[Option[User]]
    }


    // succeed
    val live0 = ZLayer.succeed(new Service {
      override def putUser(user: User): IO[UserAlreadyExist, User] = ???

      override def findUser(id: Int): UIO[Option[User]] = ???
    })

    // fromService
    val live: ZLayer[DbService, Nothing, Has[Service]] =
      ZLayer.fromService[DbService.Service, Service](dbService => new Service {
      override def putUser(user: User): IO[UserAlreadyExist, User] = ???

      override def findUser(id: Int): UIO[Option[User]] = ???
    })

    // from function
    val inMemory: ZLayer[ERef[UserAlreadyExist, Map[Int, User]], Nothing, Has[Service]] =
      ZLayer.fromFunction[ERef[UserAlreadyExist, Map[Int, User]], Service](ref => new Service {

      override def putUser(user: User): IO[UserAlreadyExist, User] = ref.getAndUpdate{map =>
        map.updated(user.id, user)
      }.as(user)

      override def findUser(id: Int): UIO[Option[User]] = ref.get.map(_.get(id)).orDie
    })

    // from Effect
    val eRef: ZLayer[Any, Nothing, Ref[Map[Int, User]]] =
      ZLayer.fromEffectMany(ZRef.make(Map[Int, User]()))


    // from serviceMany
    val many: ZLayer[DbService, Nothing, UserService with DbService] =
      ZLayer.fromServiceMany[DbService.Service, UserService with DbService] { dbService =>
      val a = new Service {
        override def putUser(user: User): IO[UserAlreadyExist, User] = ???

        override def findUser(id: Int): UIO[Option[User]] = ???
      }

      Has(a) ++ Has(dbService)

    }

  }

  def putUser(user: User): ZIO[UserService, UserAlreadyExist, User] = ZIO.accessM[UserService](_.get.putUser(user))

  def findUser(id: Int): ZIO[UserService, Nothing, Option[User]] = ZIO.accessM[UserService](_.get.findUser(id))

  case class User(id: Int, email: String)

  type MyEnv = Console with UserService

  val appLayer: ZLayer[Any, Nothing, Has[UserService.Service]] =  UserService.eRef >>> UserService.inMemory

  val app: ZIO[MyEnv, UserAlreadyExist, Unit] = for {
    _ <- putUser(User(1, "test1"))
    _ <- putUser(User(2, "test1"))
    user1 <- findUser(1)
    user2 <- findUser(2)
    _ <- console.putStrLn(user1.toString)
    _ <- console.putStrLn(user2.toString)
  } yield ()
}
