import zio.*

trait MailSubscriptionService {
  def subscribe(id: User.Id): Task[Unit]
  def unsubscribe(id: User.Id): Task[Unit]

  def getMailSubscription(id: User.Id): Task[Option[MailSubscription]]
}

object MailSubscriptionService {
  def live(): ZLayer[Dao, Throwable, MailSubscriptionService] =
    ZLayer.fromFunction { (dao: Dao) =>
      new MailSubscriptionService {
        def subscribe(id: User.Id): Task[Unit] =
          dao.addSubscription(id)

        def unsubscribe(id: User.Id): Task[Unit] = dao.removeSubscription(id)

        export dao.getMailSubscription
      }
    }
}
