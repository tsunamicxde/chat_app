import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.ConfigFactory

class MessageRepository(implicit ec: ExecutionContext) {

  private val db = Database.forConfig("db")

  val chatMessages = TableQuery[ChatMessageTable]
  val privateMessages = TableQuery[DirectMessageTable]

  def saveChatMessage(chatMessage: ChatMessage): Future[Long] =
    db.run(chatMessages returning chatMessages.map(_.id) += chatMessage)

  def savePrivateMessage(privateMessage: DirectMessage): Future[Long] =
    db.run(privateMessages returning privateMessages.map(_.id) += privateMessage)

  def close(): Future[Unit] = db.shutdown.map(_ => ())
}
