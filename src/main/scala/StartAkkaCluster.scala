import akka.actor.AddressFromURIString
import akka.actor.typed.ActorSystem
import akka.cluster.typed.{Cluster, JoinSeedNodes}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

object StartAkkaCluster {

  private var system: ActorSystem[User.Command] = _

  def getSystem: ActorSystem[User.Command] = system

  def startup(ip: String, seedPort1: Int, seedPort2: Int, port: Int, messageRepository: MessageRepository)(implicit ec: ExecutionContext): Unit = {
    val overrides = Map(
      "akka.remote.artery.canonical.hostname" -> ip,
      "akka.remote.artery.canonical.port" -> port.toString
    )

    val config: Config = ConfigFactory.parseMap(overrides.asJava)
      .withFallback(ConfigFactory.load())

    system = ActorSystem.create(User(messageRepository), "ClusterSystem", config)

    Main.setSystem(system)

    val address1 = s"akka://ClusterSystem@$ip:$seedPort1"
    val address2 = s"akka://ClusterSystem@$ip:$seedPort2"

    val seedNodes = Seq(
      AddressFromURIString.parse(address1),
      AddressFromURIString.parse(address2)
    )

    Cluster(system).manager ! JoinSeedNodes(seedNodes)
  }
}
