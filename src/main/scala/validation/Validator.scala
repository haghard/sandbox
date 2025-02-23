package validation

import Zipper.*

import scala.annotation.tailrec

sealed trait Validator[A] { self =>

  def ++[B](that: Validator[B])(implicit Z: Zipper[A, B]): Validator[Z.Out] =
    Validator.Both[A, B, Z.Out](self, that, Z)
}

object Validator {

  def apply[A](
      value: A,
      p: A => Boolean,
      errMsg: String,
    ): Validator[A] =
    Predicate(value, p, errMsg)

  final case class Predicate[A](
      value: A,
      p: A => Boolean,
      msg: String)
      extends Validator[A]

  final case class Both[A, B, C](
      lhs: Validator[A],
      rhs: Validator[B],
      zip: Zipper.WithOut[A, B, C])
      extends Validator[C]

  implicit class ValidatorOps[A](val self: Validator[A]) extends AnyVal {

    def run: Either[List[String], A] = {
      val maybeErrors = eval[A](self, List.empty[String])
      if (maybeErrors.nonEmpty) Left(maybeErrors) else Right(buildTuple(self))
    }

    private def eval[A](
        v: Validator[A],
        errors: List[String],
      ): List[String] =
      v match {
        case Validator.Predicate(v, f, errMsg) =>
          if (f(v)) errors else errMsg :: errors

        case both: Validator.Both[a, b, c] =>
          eval(both.lhs, errors) ++ eval(both.rhs, errors)
      }

    private def buildTuple(v: Validator[A]): A =
      v match {
        case Validator.Predicate(v, _, _) =>
          v
        case b: Validator.Both[_, _, _] =>
          val size = b.zip.size
          val array = Array.ofDim[Any](size)
          initArray(0, array, v, List.empty)
          b.zip.toTuple(array)
      }

    @tailrec final def initArray(
        ind: Int,
        array: Array[Any],
        v: Validator[_],
        next: List[Validator[_]],
      ): Array[Any] =
      v match {
        case Both(lhs, rhs, _) =>
          initArray(ind, array, lhs, rhs :: next)
        case c: Predicate[_] =>
          // println(s"$ind - ${c.value}")
          array.update(ind, c.value)
          next match {
            case h :: tail =>
              initArray(ind + 1, array, h, tail)
            case Nil =>
              array
          }
      }
  }

  implicit class Ops[A](val v: A) extends AnyVal {

    def validate(errMsg: String)(f: A => Boolean): Validator[A] =
      apply(v, f, errMsg)
  }
}

object ValidatorProgram extends App {
  import Validator._

  val row = DbRow(3, 42.2, "hello", Some(0xff.toByte), List(1, 2, 3))

  val validator =
    row.a.validate("aaa")(_ > -4) ++ row.b.validate("bbb")(_ == 42.2) ++
      row.c.validate("ccc")(_.startsWith("he")) ++ row.aOpt.validate("ddd")(_.isDefined) ++
      row.digits.validate("eee")(_.forall(_ > 0))

  println(validator.run)
}
