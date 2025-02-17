package validation

trait TupleSize[A] {
  def size: Int
}

object TupleSize {

  implicit def unit: TupleSize[Unit] = new TupleSize[Unit] {
    val size = 0
  }

  implicit def tupleSize[A]: TupleSize[A] = new TupleSize[A] {
    val size = 1
  }

  implicit def tupleSize2[A, B]: TupleSize[(A, B)] = new TupleSize[(A, B)] {
    val size = 2
  }

  implicit def tupleSize3[A, B, C]: TupleSize[(A, B, C)] = new TupleSize[(A, B, C)] {
    val size = 3
  }

  implicit def tupleSize4[A, B, C, D]: TupleSize[(A, B, C, D)] = new TupleSize[(A, B, C, D)] {
    val size = 4
  }

  implicit def tupleSize5[A, B, C, D, E]: TupleSize[(A, B, C, D, E)] = new TupleSize[(A, B, C, D, E)] {
    val size = 5
  }

  def apply[A](implicit ts: TupleSize[A]): TupleSize[A] = ts
}
