case class State(
  users: Map[User.Id, User],
  emails: Map[Email, User.Id],
  lessons: Map[User.Id, List[Lesson]],
  subscriptions: Map[User.Id, MailSubscription]
)

object State {

  extension (s: State)
    def getUserByEmail(email: Email): Option[User] =
      s.emails.get(email).flatMap(s.users.get)

  def empty: State = State(
    users = Map.empty,
    emails = Map.empty,
    lessons = Map.empty,
    subscriptions = Map.empty
  )
}
