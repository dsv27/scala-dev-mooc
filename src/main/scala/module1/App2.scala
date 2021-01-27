package module1
import org.slf4j.LoggerFactory
import zio.internal.Platform
import zio.{ExitCode, URIO, ZIO}

object App2 extends zio.App {

  val logger = LoggerFactory.getLogger("ZIO APP")

  override val platform: Platform = Platform.default.withReportFailure{cause =>

    if(cause.died) {
      logger.error(cause.prettyPrint)
    }
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = zio.console.putStrLn("Hello") *> ZIO.dieMessage("Oooops")
}
