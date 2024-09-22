import zio.prelude.*

type Password = Password.Type
object Password extends Newtype[String]
