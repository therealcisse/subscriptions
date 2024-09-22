import zio.test.magnolia.*
import zio.test.*
import zio.*

object DaoSpec extends ZIOSpecDefault {
  def spec =
    suite("DaoSpec")(
      test("add subscription") {
        check(genEmail, genPassword) { case (email, password) =>
          (ZIO.service[Dao.StateHolder] <&> ZIO.service[Dao]) flatMap { case (s, dao) =>
            for {
              now <- Clock.instant

              initialState <- s.get

              id <- dao.addUser(email, password)
              _  <- dao.addSubscription(id)

              user = User(id = id, email = email, password = password)

              expected = initialState.copy(
                users = initialState.users + (id                 -> user),
                emails = initialState.emails + (email            -> id),
                subscriptions = initialState.subscriptions + (id -> MailSubscription(id, now))
              )

              a <- assertZIO(s.get)(Assertion.equalTo(expected))
            } yield a
          }

        }.provideLayer(
          Dao.StateHolder.make >+> Dao.live()
        )
      },
      test("remove subscription") {
        check(genEmail, genPassword) { case (email, password) =>
          (ZIO.service[Dao.StateHolder] <&> ZIO.service[Dao]) flatMap { case (s, dao) =>
            for {
              now <- Clock.instant

              initialState <- s.get

              id <- dao.addUser(email, password)
              _  <- dao.addSubscription(id)
              user = User(id = id, email = email, password = password)

              expected = initialState.copy(
                users = initialState.users + (id                 -> user),
                emails = initialState.emails + (email            -> id),
                subscriptions = initialState.subscriptions + (id -> MailSubscription(id, now))
              )

              added <- assertZIO(s.get)(Assertion.equalTo(expected))

              _ <- dao.removeSubscription(id)

              expected1 = expected.copy(subscriptions = expected.subscriptions - id)

              removed <- assertZIO(s.get)(Assertion.equalTo(expected1))
            } yield added && removed
          }

        }.provideLayer(
          Dao.StateHolder.make >+> Dao.live()
        )
      },
      test("duplicate subscription") {
        check(genEmail, genPassword, genPassword) { case (email, password1, password2) =>
          (ZIO.service[Dao.StateHolder] <&> ZIO.service[Dao]) flatMap { case (s, dao) =>
            for {
              now <- Clock.instant

              initialState <- s.get

              id <- dao.addUser(email, password1)
              _  <- dao.addSubscription(id)
              user = User(id = id, email = email, password = password1)

              expected = initialState.copy(
                users = initialState.users + (id                 -> user),
                emails = initialState.emails + (email            -> id),
                subscriptions = initialState.subscriptions + (id -> MailSubscription(id, now))
              )

              added <- assertZIO(s.get)(Assertion.equalTo(expected))

              exit <- dao.addSubscription(id).exit

              removed <- assertTrue(exit == Exit.fail(DuplicateSubscription(id)))
            } yield added && removed
          }

        }.provideLayer(
          Dao.StateHolder.make >+> Dao.live()
        )
      },
      test("duplicate user") {
        check(genEmail, genPassword, genPassword) { case (email, password1, password2) =>
          (ZIO.service[Dao.StateHolder] <&> ZIO.service[Dao]) flatMap { case (s, dao) =>
            for {
              _    <- dao.addUser(email, password1)
              exit <- dao.addUser(email, password2).exit

              a <- assertTrue(exit == Exit.fail(DuplicateEmail(email)))
            } yield a
          }

        }.provideLayer(
          Dao.StateHolder.make >+> Dao.live()
        )
      }
    )
}
