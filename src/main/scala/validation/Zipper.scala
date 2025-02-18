package validation

sealed trait Zipper[A, B] {
  type Out
}

object Zipper extends ZipperEntryPoint {

  type WithOut[A, B, Out0] = Zipper[A, B] { type Out = Out0 }

  final case class Zipper2[A, B, C]() extends Zipper[(A, B), C] {
    type Out = (A, B, C)
  }

  implicit def zipper2[A, B, C]: Zipper.WithOut[(A, B), C, (A, B, C)] = Zipper2()

  final case class Zipper3[A, B, C, D]() extends Zipper[(A, B, C), D] {
    type Out = (A, B, C, D)
  }

  implicit def zipper3[A, B, C, D]: Zipper.WithOut[(A, B, C), D, (A, B, C, D)] = Zipper3()

  final case class Zipper4[A, B, C, D, E]() extends Zipper[(A, B, C, D), E] {
    type Out = (A, B, C, D, E)
  }

  implicit def zipper4[A, B, C, D, E]: Zipper.WithOut[(A, B, C, D), E, (A, B, C, D, E)] = Zipper4()

  final case class Zipper5[A, B, C, D, E, F]() extends Zipper[(A, B, C, D, E), F] {
    type Out = (A, B, C, D, E, F)
  }

  implicit def zipper5[A, B, C, D, E, F]: Zipper.WithOut[(A, B, C, D, E), F, (A, B, C, D, E, F)] = Zipper5()
}

trait ZipperEntryPoint {
  implicit def zipper1[A, B]: Zipper.WithOut[A, B, (A, B)] = ZipperEntryPoint.Zipper1[A, B]()
}

object ZipperEntryPoint {
  final case class Zipper1[A, B]() extends Zipper[A, B] {
    type Out = (A, B)
  }
}
