import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.MemberRemoved
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.ClusterEvent.ReachabilityEvent
import akka.cluster.ClusterEvent.ReachableMember
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.typed.Cluster
import akka.cluster.typed.Subscribe

object UserListener {

  sealed trait Event
  private final case class ReachabilityChange(reachabilityEvent: ReachabilityEvent) extends Event
  private final case class MemberChange(event: MemberEvent) extends Event
  case class BroadcastRefUser(actorRef: ActorRef[User.Command], name: String) extends Event with CborSerializable
  case class RefUser(actorRef: ActorRef[User.Command], name: String) extends Event with CborSerializable
  case class MembersChat(membersChat: java.util.Map[java.lang.String, ActorRef[User.Command]]) extends Event with CborSerializable

  def apply(name: String): Behavior[Event] = Behaviors.setup { ctx =>
    val topicRef: ActorRef[Topic.Command[RefUser]] = ctx.spawn(Topic[RefUser]("chatRef"), "chatRef");
    topicRef ! Topic.Subscribe(ctx.self)

    val topicMember: ActorRef[Topic.Command[MembersChat]] = ctx.spawn(Topic[MembersChat]("member"), "member");
    topicMember ! Topic.Subscribe(ctx.self)

    val memberEventAdapter: ActorRef[MemberEvent] = ctx.messageAdapter(MemberChange)
    Cluster(ctx.system).subscriptions ! Subscribe(memberEventAdapter, classOf[MemberEvent])

    val reachabilityAdapter = ctx.messageAdapter(ReachabilityChange)
    Cluster(ctx.system).subscriptions ! Subscribe(reachabilityAdapter, classOf[ReachabilityEvent])

    Behaviors.receiveMessage { message =>
      message match {
        case BroadcastRefUser(actorRef, username) =>
          Thread.sleep(3000);
          topicRef ! Topic.Publish(RefUser(actorRef, username))
          Behaviors.same

        case RefUser(actorRef, username) =>
          Main.members.put(username, actorRef)
          Behaviors.same

        case MembersChat(member) =>
          if(Main.members.size() < member.size()) {
            Main.members = member
          }

        case ReachabilityChange(reachabilityEvent) =>
          reachabilityEvent match {
            case UnreachableMember(member) =>
              ctx.log.info("Member detected as unreachable: {}", member)
            case ReachableMember(member) =>
              ctx.log.info("Member back to reachable: {}", member)
          }

        case MemberChange(changeEvent) =>
          changeEvent match {
            case MemberUp(member) =>
              Thread.sleep(2000);
              topicMember ! Topic.Publish(MembersChat(Main.members))
              ctx.log.info("Member is Up: {}", member.address)

            case MemberRemoved(member, previousStatus) =>
              ctx.log.info("Member is Removed: {} after {}",
                member.address, previousStatus)
            case _: MemberEvent =>
          }
      }
      Behaviors.same
    }
  }
}