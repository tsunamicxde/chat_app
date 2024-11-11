import slick.jdbc.PostgresProfile.api._

class ChatMessageTable(tag: Tag) extends Table[ChatMessage](tag, "chat_messages") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def message = column[String]("message")
  def username = column[String]("username")
  def seedPort1 = column[Int]("seed_port1")
  def seedPort2 = column[Int]("seed_port2")

  def * = (id, message, username, seedPort1, seedPort2) <> (ChatMessage.tupled, ChatMessage.unapply)
}
