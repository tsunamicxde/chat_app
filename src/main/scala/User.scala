import User.{Command, Message, PrivateMessage, Join, JoinPrivateChat}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import javafx.application.Platform
import javafx.stage.Stage


object User {

  sealed trait Command
  case class Message(msg: String, name: String) extends Command with CborSerializable
  case class PrivateMessage(msg: String, userNamePrivateChat: String, outMessageName: String) extends Command with CborSerializable
  case class Join(username: String) extends Command with CborSerializable
  case class JoinPrivateChat(username: String) extends Command with CborSerializable

  def apply(): Behavior[Command] =
    Behaviors.setup{ context =>
      val user = new User(context)
      user
    }
}

class User(context: ActorContext[Command])
  extends AbstractBehavior[User.Command](context) {

  var chat: ActorRef[Chat.Command] = context.spawn(Chat(name = ""), "chat")
  val userListener: ActorRef[UserListener.Event] = context.spawn(UserListener(name = ""), "userListener")


  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Message(msg, name) =>
        chat ! Chat.BroadcastMessage(msg, name)
        Behaviors.same

      case PrivateMessage(msg, userNamePrivateChat, outMessageName) =>
        Platform.runLater(new Runnable {
          override def run(): Unit = {
            Main.directArea
              .get(userNamePrivateChat)
              .appendText(s"[Direct]: [$outMessageName]: $msg\n")
          }
        })
        Behaviors.same

      case Join(username) =>
        chat ! Chat.BroadcastMessage("joined the chat", username)
        chat ! Chat.Join()
        userListener ! UserListener.BroadcastRefUser(context.self, username)
        Behaviors.same

      case JoinPrivateChat(username) =>
        Platform.runLater(new Runnable {
          override def run(): Unit = {
            DirectPanel.startPrivateChat(new Stage(), username);
          }
        })
        Behaviors.same
    }
  }
}