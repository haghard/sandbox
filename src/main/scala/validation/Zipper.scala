package validation

sealed trait Zipper[A, B] {
  type Out

  def size: Int
  def toTuple(ar: Array[Any]): Out
}

object Zipper extends ZipperEntryPoint {

  type WithOut[A, B, Out0] = Zipper[A, B] { type Out = Out0 }

  final case class Zipper3[A, B, C]() extends Zipper[(A, B), C] {
    type Out = (A, B, C)
    val size = 3

    def toTuple(ar: Array[Any]): Out =
      (ar(0), ar(1), ar(2)).asInstanceOf[Out]
  }

  implicit def _3[A, B, C]: Zipper.WithOut[(A, B), C, (A, B, C)] = Zipper3()

  final case class Zipper4[A, B, C, D]() extends Zipper[(A, B, C), D] {
    type Out = (A, B, C, D)
    val size = 4

    def toTuple(ar: Array[Any]): Out =
      (ar(0), ar(1), ar(2), ar(3)).asInstanceOf[Out]
  }

  implicit def _4[A, B, C, D]: Zipper.WithOut[(A, B, C), D, (A, B, C, D)] = Zipper4()

  final case class Zipper5[A, B, C, D, E]() extends Zipper[(A, B, C, D), E] {
    type Out = (A, B, C, D, E)
    val size = 5

    def toTuple(ar: Array[Any]): Out =
      (ar(0), ar(1), ar(2), ar(3), ar(4)).asInstanceOf[Out]
  }

  implicit def _5[A, B, C, D, E]: Zipper.WithOut[(A, B, C, D), E, (A, B, C, D, E)] = Zipper5()

  final case class Zipper6[A, B, C, D, E, F]() extends Zipper[(A, B, C, D, E), F] {
    type Out = (A, B, C, D, E, F)
    val size = 6

    def toTuple(ar: Array[Any]): Out =
      (ar(0), ar(1), ar(2), ar(3), ar(4), ar(5)).asInstanceOf[Out]
  }

  implicit def _6[A, B, C, D, E, F]: Zipper.WithOut[(A, B, C, D, E), F, (A, B, C, D, E, F)] =
    Zipper6()
}

trait ZipperEntryPoint {
  implicit def _2[A, B]: Zipper.WithOut[A, B, (A, B)] = ZipperEntryPoint.Zipper2[A, B]()
}

object ZipperEntryPoint {
  final case class Zipper2[A, B]() extends Zipper[A, B] {
    type Out = (A, B)
    val size = 2
    def toTuple(ar: Array[Any]): Out =
      (ar(0), ar(1)).asInstanceOf[Out]
  }
}
