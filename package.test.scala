import zio.test.magnolia.*
import zio.test.*
import zio.*

val faker = com.github.javafaker.Faker()

val genEmail: Gen[Any, Email]       = Gen.const(faker.internet().emailAddress()).map(Email(_))
val genPassword: Gen[Any, Password] = Gen.const(faker.internet().password(8, 20)).map(Password(_))
val genId: Gen[Any, User.Id]        = Gen.fromZIO(User.Id.gen.orDie)

val genUser: Gen[Any, User] = (genId <*> genEmail <*> genPassword).map(User.apply)
