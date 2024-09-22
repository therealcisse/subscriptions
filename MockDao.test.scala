import zio.mock.*
import zio.*
import zio.test.*

object MockDao extends Mock[Dao] {
  object AddUser extends Effect[(Email, Password), Throwable, User.Id]

  val compose: URLayer[Proxy, Dao] =
    ZLayer {
      for {
        proxy <- ZIO.service[Proxy]
      } yield new Dao {
        def addUser(email: Email, password: Password): Task[User.Id] =
          proxy(AddUser, email, password)

        def findByEmail(email: Email): Task[Option[User]]                    = ???
        def findById(id: User.Id): Task[Option[User]]                        = ???
        def delUser(id: User.Id): Task[Unit]                                 = ???
        def getUsers(): Task[List[User]]                                     = ???
        def removeSubscription(id: User.Id): Task[Unit]                      = ???
        def addSubscription(id: User.Id): Task[Unit]                         = ???
        def getMailSubscription(id: User.Id): Task[Option[MailSubscription]] = ???

      }
    }
}
