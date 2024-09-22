import zio.test.magnolia.*
import zio.test.*
import zio.mock.*
import zio.*

object UserServiceSpec extends ZIOSpecDefault {

  def spec =
    suite("UserServiceSpec")(
      test("add user") {
        check(genEmail, genPassword) { case (email, password) =>
          for {
            expecteId <- User.Id.gen

            sut             = UserService.add(email, password)
            liveUserService = UserService.live()
            mockUserService = MockDao.AddUser(
              assertion = Assertion.equalTo((email, password)),
              result = Expectation.value(expecteId)
            )

            id <- sut.provide(liveUserService, mockUserService)
          } yield assertTrue(id == expecteId)
        }
      }
    )
}
