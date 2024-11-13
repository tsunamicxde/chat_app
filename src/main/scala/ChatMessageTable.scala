import slick.jdbc.PostgresProfile.api._

class ChatMessageTable(tag: Tag) extends Table[ChatMessage](tag, "chat_messages") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def message = column[String]("message")
  def username = column[String]("username")

  def * = (id, message, username) <> (ChatMessage.tupled, ChatMessage.unapply)
}
