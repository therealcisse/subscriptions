import java.util.UUID

case class MailSubscription(
  id: User.Id,
  timestamp: java.time.Instant
)

object MailSubscription {}
