package module5.admissionPlanService.services

import module5.abiturientService.dtos.AbiturientRequestDTO
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.interop.console.cats.putStrLn
import zio.{Task, ZIO, ZLayer}
import zio.kafka.consumer.{Consumer, ConsumerSettings, Subscription}
import zio.kafka.serde.Serde
import zio.stream.ZStream

object AbiturientRequestConsumer {
  import io.circe.syntax._

  // Настройки консьюмера
  val consumerSettings = ConsumerSettings(List("localhost:9092"))
    .withGroupId("admissionPlanService")

  // Слой консьюмера
  val live = ZLayer.fromManaged(Consumer.make(consumerSettings))

  // Эмулируем метод отправки сообщения
  def reservePlace(fio: String) =
    putStrLn(s"Place is reserved for $fio")

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
          _ <- reservePlace(r.fio)
        }yield ()
      )
    }.runDrain.provideSomeLayer[Blocking with Console with Clock](live)

}
