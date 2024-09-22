import zio.*

trait LessonService {
  def subscribe(id: User.Id, lesson: Lesson): Task[Unit]
  def unsubscribe(id: User.Id, lesson: Lesson): Task[Unit]
  def getLessons(id: User.Id): Task[List[Lesson]]
  def getAllLessons(): Task[List[Lesson]]
}

object LessonService {
  // def live(): ZLayer[Dao, Throwable, LessonService] =
  //   ZLayer.fromFunction { (dao: Dao) =>
  //     new LessonService {
  //     }
  //   }
}
