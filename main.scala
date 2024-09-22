import cats.implicits.*

import zio.*

@main def main: Unit =
  val runtime = Runtime.default

  Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run(???).getOrThrowFiberFailure()

  }
