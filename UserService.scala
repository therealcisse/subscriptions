import zio.*

trait UserService {
  def add(email: Email, password: Password): Task[User.Id]

  def findById(id: User.Id): Task[Option[User]]
  def findByEmail(email: Email): Task[Option[User]]
  def del(id: User.Id): Task[Unit]
  def getUsers(): Task[List[User]]
}

object UserService {
  def add(email: Email, password: Password): RIO[UserService, User.Id] =
    ZIO.serviceWithZIO(_.add(email, password))

  def live(): ZLayer[Dao, Throwable, UserService] =
    ZLayer.fromFunction { (dao: Dao) =>
      new UserService {
        def add(email: Email, password: Password): Task[User.Id] =
          dao.addUser(email, password)

        export dao.findByEmail
        export dao.findById

        def del(id: User.Id): Task[Unit] = dao.delUser(id)
        def getUsers(): Task[List[User]] = dao.getUsers()
      }
    }
}
