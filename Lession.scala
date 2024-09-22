import zio.prelude.*

type Lesson = Lesson.Type
object Lesson extends Newtype[String]
