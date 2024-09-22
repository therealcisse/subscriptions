case class User(
  id: User.Id,
  email: Email,
  password: Password
)

object User {
  import zio.*
  import zio.prelude.*
  import io.github.thibaultmeyer.cuid.CUID

  type Id = Id.Type
  object Id extends Newtype[CUID] {
    def gen: Task[Id] = ZIO.attempt(Id(CUID.randomCUID2(32)))

  }

  def gen(email: Email, password: Password): Task[User] =
    Id.gen.map(id => User(id = id, email = email, password = password))
}
