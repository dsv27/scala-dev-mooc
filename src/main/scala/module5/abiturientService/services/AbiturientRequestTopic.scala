package module5.abiturientService.services

import module5.abiturientService.dtos.AbiturientRequestDTO
import org.apache.kafka.clients.producer.RecordMetadata
import zio.blocking.Blocking
import zio.{Has, RIO, ULayer, ZIO, ZLayer}
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde

object AbiturientRequestTopic {

  import io.circe.syntax._
  type AbiturientRequestTopic = Has[Service]

  trait Service{
    def send(abiturientRequestDTO: AbiturientRequestDTO): RIO[Blocking, RecordMetadata]
  }


  // Настройки продьюсера для Кафки
  private val producerSettings = ProducerSettings(List("localhost:9092"))

  // Слой продьюсера
  val producerLive =
    ZLayer.fromManaged(Producer.make(producerSettings, Serde.string, Serde.string))

  // Реализация сервиса
  class KafkaServiceImpl extends Service{
    override def send(abiturientRequestDTO: AbiturientRequestDTO): RIO[Blocking, RecordMetadata] =
      Producer.produce[Any, String, String]("abiturient-request", "1",
        abiturientRequestDTO.asJson.toString()).provideSomeLayer[Blocking](producerLive)
  }

  // Слой сервиса
  val live: ULayer[AbiturientRequestTopic] = ZLayer.succeed(new KafkaServiceImpl)

  // accessible pattern
  def send(abiturientRequestDTO: AbiturientRequestDTO) =
    ZIO.accessM[AbiturientRequestTopic with Blocking](_.get.send(abiturientRequestDTO))
}
