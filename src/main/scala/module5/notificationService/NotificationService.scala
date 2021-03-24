package module5.notificationService

import module5.abiturientService.dtos.AbiturientRequestDTO
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.interop.console.cats.putStrLn
import zio.kafka.consumer.{Consumer, ConsumerSettings, Subscription}
import zio.kafka.serde.Serde
import zio.stream.ZStream
import zio.{ExitCode, URIO, ZIO, ZLayer}

object NotificationService extends zio.App{

  import io.circe.syntax._

  // Настройки консьюмера
  val consumerSettings = ConsumerSettings(List("localhost:9092"))
    .withGroupId("notificationService")

  // Слой консьюмера
  val live = ZLayer.fromManaged(Consumer.make(consumerSettings))

  // Эмулируем метод отправки сообщения
  def sendNotification(fio: String) = putStrLn(s"Notification sent to $fio")

  // Стрим читающий сообщения из топика
  val consumeRequests: ZIO[Clock with Blocking with Console, Throwable, Unit] =
    Consumer.subscribeAnd(Subscription.topics("abiturient-request"))
      .plainStream(Serde.string, Serde.string).flatMap{ record =>
         val request = ZIO.fromEither(io.circe.parser.parse(record.value)).flatMap(j =>
          ZIO.fromEither(j.as[AbiturientRequestDTO])
         )
         ZStream.fromEffect(
           for{
             r <- request
             _ <- sendNotification(r.fio)
           }yield ()
         )
    }.runDrain.provideSomeLayer[Blocking with Console with Clock](live)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = consumeRequests.exitCode
}
