import zio.prelude.*

type Email = Email.Type
object Email extends Newtype[String]
