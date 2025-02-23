package flight

@scala.annotation.implicitNotFound("Flight constraint violation: Found ${Out} but expected ${In}")
sealed trait IsEqualTo[-In, +Out]

object IsEqualTo {
  implicit def nsub[A]: IsEqualTo[A, A] = new IsEqualTo[A, A] {}
}
