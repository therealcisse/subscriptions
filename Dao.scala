import zio.*

trait Dao {
  def findByEmail(email: Email): Task[Option[User]]
  def findById(id: User.Id): Task[Option[User]]
  def delUser(id: User.Id): Task[Unit]
  def getUsers(): Task[List[User]]
  def removeSubscription(id: User.Id): Task[Unit]
  def addUser(email: Email, password: Password): Task[User.Id]
  def addSubscription(id: User.Id): Task[Unit]
  def getMailSubscription(id: User.Id): Task[Option[MailSubscription]]
}

object Dao {
  case class StateHolder(ref: Ref.Synchronized[State]) {
    export ref.*
  }

  object StateHolder {
    def make: ZLayer[Any, Nothing, StateHolder] =
      ZLayer {
        Ref.Synchronized.make(State.empty).map(ref => Dao.StateHolder(ref = ref))

      }

  }

  def live(): ZLayer[StateHolder, Throwable, Dao] =
    ZLayer.fromFunction { (ref: StateHolder) =>
      new Dao {
        def findByEmail(email: Email): Task[Option[User]] =
          ref.get.map(state => state.emails.get(email).flatMap(state.users.get))

        def findById(id: User.Id): Task[Option[User]] =
          ref.get.map(_.users.get(id))

        def delUser(id: User.Id): Task[Unit] =
          ref.updateZIO { case s @ State(users, emails, _, subscriptions) =>
            users.get(id) match {
              case None => ZIO.fail(UserNotFound())
              case Some(user) =>
                ZIO.succeed(
                  s.copy(
                    users = users - user.id,
                    emails = emails - user.email,
                    subscriptions = subscriptions - user.id
                  )
                )
            }

          }

        def getUsers(): Task[List[User]] =
          ref.get.map(_.users.values.toList)

        def removeSubscription(id: User.Id): Task[Unit] =
          ref.updateZIO { case s @ State(_, _, _, subscriptions) =>
            if subscriptions.contains(id) then
              ZIO.succeed(s.copy(subscriptions = subscriptions - id))
            else ZIO.fail(SubscriptionNotFound())

          }

        def addUser(email: Email, password: Password): Task[User.Id] =
          ref.modifyZIO { case s @ State(users, emails, _, subscriptions) =>
            s.getUserByEmail(email) match {
              case None =>
                User.gen(email, password) map { user =>
                  user.id -> s.copy(
                    users = users + (user.id -> user),
                    emails = emails + (email -> user.id),
                    subscriptions = subscriptions
                  )

                }

              case Some(user) =>
                ZIO.fail(DuplicateEmail(email))
            }

          }

        def addSubscription(id: User.Id): Task[Unit] =
          ref.updateZIO { case s @ State(users, emails, _, subscriptions) =>
            if subscriptions.contains(id) then ZIO.fail(DuplicateSubscription(id))
            else
              Clock.instant.map { now =>
                s.copy(subscriptions = subscriptions + (id -> MailSubscription(id, now)))

              }

          }

        def getMailSubscription(id: User.Id): Task[Option[MailSubscription]] =
          ref.get.map(_.subscriptions.get(id))

      }

    }
}
