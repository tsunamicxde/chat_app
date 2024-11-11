import slick.jdbc.PostgresProfile.api._

class DirectMessageTable(tag: Tag) extends Table[DirectMessage](tag, "private_messages") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def message = column[String]("message")
  def sender = column[String]("sender")
  def recipient = column[String]("recipient")

  def * = (id, message, sender, recipient) <> (DirectMessage.tupled, DirectMessage.unapply)
}