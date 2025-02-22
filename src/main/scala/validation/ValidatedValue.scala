package validation

import ValidatedValue._

sealed trait ValidatedValue[+A] { self =>

  def flatMap[B](f: A => ValidatedValue[B]): ValidatedValue[B] =
    self match {
      case Valid(v) =>
        f(v)
      case e: Invalid =>
        e
    }

  def map[B](f: A => B): ValidatedValue[B] =
    self match {
      case ok: Valid[a] =>
        Valid(f(ok.v))
      case e @ Invalid(_) =>
        e
    }

  def <>[B](that: ValidatedValue[B]): ValidatedValue[(A, B)] =
    self.zip(that)

  def zip[B](that: ValidatedValue[B]): ValidatedValue[(A, B)] =
    (self, that) match {
      case (Valid(value1), Valid(value2)) =>
        Valid((value1, value2))
      case (Invalid(errors1), Invalid(errors2)) =>
        Invalid(errors1 ++ errors2)
      case (Invalid(errors), _) =>
        Invalid(errors)
      case (_, Invalid(errors)) =>
        Invalid(errors)
    }

  def zipWith[B, C](that: ValidatedValue[B])(f: (A, B) => C): ValidatedValue[C] =
    (self zip that).map(f.tupled)

  def flatten[A1 >: A, Out](implicit F: Flatten.Aux[A1, Out]): ValidatedValue[Out] =
    self.map(F.apply(_))
}

object ValidatedValue {
  final case class Valid[+A](v: A) extends ValidatedValue[A]
  final case class Invalid(errors: List[String]) extends ValidatedValue[Nothing]

  def fromOpt[A](opt: Option[A], error: String): ValidatedValue[A] =
    opt match {
      case None    => Invalid(error :: Nil)
      case Some(v) => Valid(v)
    }

  implicit class ValueOps[+T](private val v: T) extends AnyVal {
    def validate(precondition: String)(f: T => Boolean): ValidatedValue[T] =
      if (f(v)) ValidatedValue.Valid(v) else ValidatedValue.Invalid(List(precondition))
  }

  implicit class ListOps[+T](private val list: List[T]) extends AnyVal {
    def validate(precondition: String)(f: T => Boolean): ValidatedValue[List[T]] = {
      val (_, errs) =
        list
          .map(r => r.validate(s"$precondition but found $r")(f))
          .foldLeft((List.empty[Valid[_]], List.empty[Invalid])) {
            case ((l, r), c) =>
              c match {
                case ok: Valid[_] => (ok :: l, r)
                case err: Invalid => (l, err :: r)
              }
          }

      if (errs.isEmpty) Valid(list) else Invalid(errs.flatMap(_.errors))
    }
  }
}

object ValidatedValueProgram extends App {
  import Flatten._

  val row = DbRow(1, 42.2, "hello", Some(0xff.toByte), List(1, 2, 3))
  val maybeRow: Option[DbRow] = Some(row) // None

  val err =
    ValidatedValue.fromOpt(maybeRow, "Empty row").flatMap { row =>
      row
        .a
        .validate("aaa")(_ > 4)
        .zipWith(row.b.validate("bbb")(_ == 42.2))((_, b) => b) // lose a
        .zip(row.c.validate("ccc")(_.startsWith("h")))
        .zip(row.aOpt.validate("ddd")(_.find(_ < 0x1f.toByte).nonEmpty))
        .zip(row.digits.size.validate("nonEmpty")(_ > 0))
        .zip(row.digits.validate("ShouldBe > 2")(_ > 2))
        .flatten
    }

  println("res1: " + err)

  val ok =
    ValidatedValue.fromOpt(maybeRow, "Empty row").flatMap { row =>

      row
        .a
        .validate("aaa")(_ > -4)
        .zip(row.b.validate("bbb")(_ != 43.2))
        .flatten

      row
        .a
        .validate("aaa")(_ > -4)
        .zipWith(row.b.validate("bbb")(_ != 43.2))((_, b) => b) // lose a
        .zip(row.c.validate("ccc")(_.startsWith("h")))
        .zip(row.aOpt.validate("ddd")(_.find(_ < 0x1f.toByte).nonEmpty))
        .zip(row.digits.size.validate("nonEmpty")(_ > 0))
        .zip(row.digits.validate("ShouldBe < 5")(_ < 5))
        .flatten
    }

  println("res2: " + ok)

}
