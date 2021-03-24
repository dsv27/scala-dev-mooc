package module5.abiturientService.abiturientRequestSaga

import zio.{Task, ZIO}
import zio.console.Console
import zio.interop.console.cats.putStrLn

object AbiturientRequestSaga {

  /**
   * Запрос к сервису приема на резервацию места
   */
  val reservePlace = putStrLn("AdmissionPlan - место зарезервировано") *> Task.effect()

  /**
   * Запрос к сервису модерации на проверку досье
   */
  val checkDossier = putStrLn("ModerationService - досье проверено") *> Task.fail(new Throwable("Ooops"))

  /**
   * Запрос к сервису приема на отмену резервации места
   */
  val cancelPlaceReservation = putStrLn("AdmissionPlan - резервирование отменено") *> Task.effect()

  /**
   * Запрос к сервису модерации на анулирование проверки
   */
  val cancelDossierCheck = putStrLn("ModerationService - проверка анулирована") *> Task.effect()


  /**
   * Вариант 1
   */

  lazy val saga1 = for{
    _ <- reservePlace orElse cancelPlaceReservation
    _ <- checkDossier orElse cancelDossierCheck
  } yield ()

  import com.vladkopanev.zio.saga.Saga._


  val saga2 = (for {
    _ <- reservePlace compensate cancelPlaceReservation
    _ <- checkDossier compensate cancelDossierCheck
  } yield ()).transact

}
