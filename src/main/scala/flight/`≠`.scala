package flight

abstract class `≠`[A, B] extends Serializable

object `≠` {
  implicit def nsub[A, B]: A `≠` B = new `≠`[A, B] {}

  @scala.annotation.implicitAmbiguous("Flight constraint violation: A flight cannot start at ${A} and stop at ${B}")
  implicit def nsubAmbig1[A, B >: A]: A `≠` B = sys.error("Unexpected call")

  implicit def nsubAmbig2[A, B >: A]: A `≠` B = sys.error("Unexpected call")
}
