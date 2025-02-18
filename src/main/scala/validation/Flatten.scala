package validation

// https://github.com/mattlianje/etl4s/blob/master/Etl4s.scala#L408
trait Flatten[A] {
  type Out

  def apply(a: A): Out
}

trait P0 {
  implicit def zero[A]: Flatten.Aux[A, A] = new Flatten[A] {
    type Out = A
    def apply(a: A): A = a
  }
}

trait P1 extends P0 {
  implicit def tuple3[A, B, C]: Flatten.Aux[((A, B), C), (A, B, C)] =
    new Flatten[((A, B), C)] {
      type Out = (A, B, C)
      def apply(t: ((A, B), C)): (A, B, C) = {
        val ((a, b), c) = t
        (a, b, c)
      }
    }
}

trait P2 extends P1 {
  implicit def tuple4[A, B, C, D]: Flatten.Aux[(((A, B), C), D), (A, B, C, D)] =
    new Flatten[(((A, B), C), D)] {
      type Out = (A, B, C, D)
      def apply(t: (((A, B), C), D)): (A, B, C, D) = {
        val (((a, b), c), d) = t
        (a, b, c, d)
      }
    }
}

trait P3 extends P2 {
  implicit def tuple5[A, B, C, D, E]: Flatten.Aux[((((A, B), C), D), E), (A, B, C, D, E)] =
    new Flatten[((((A, B), C), D), E)] {
      type Out = (A, B, C, D, E)
      def apply(t: ((((A, B), C), D), E)): (A, B, C, D, E) = {
        val ((((a, b), c), d), e) = t
        (a, b, c, d, e)
      }
    }
}

trait P4 extends P3 {
  implicit def tuple6[A, B, C, D, E, F]: Flatten.Aux[(((((A, B), C), D), E), F), (A, B, C, D, E, F)] =
    new Flatten[(((((A, B), C), D), E), F)] {
      type Out = (A, B, C, D, E, F)
      def apply(t: (((((A, B), C), D), E), F)): (A, B, C, D, E, F) = {
        val (((((a, b), c), d), e), f) = t
        (a, b, c, d, e, f)
      }
    }
}

trait P5 extends P4 {
  implicit def tuple7[A, B, C, D, E, F, G]: Flatten.Aux[((((((A, B), C), D), E), F), G), (A, B, C, D, E, F, G)] =
    new Flatten[((((((A, B), C), D), E), F), G)] {
      type Out = (A, B, C, D, E, F, G)
      def apply(t: ((((((A, B), C), D), E), F), G)): (A, B, C, D, E, F, G) = {
        val ((((((a, b), c), d), e), f), g) = t
        (a, b, c, d, e, f, g)
      }
    }
}

object Flatten extends P5 {
  type Aux[A, B] = Flatten[A] { type Out = B }
}
