package validation

import TupleSize._
import Zipper._

sealed trait Validator[A] { self =>

  def ++[B](that: Validator[B])(implicit Z: Zipper[A, B]): Validator[Z.Out] =
    Validator.Both[A, B, Z.Out](self, that, Z)

  def tupleSize: Int =
    self match {
      case Validator.Predicate(_, _, _, tupleSize) => tupleSize.size
      case Validator.Both(left, right, _)          => left.tupleSize + right.tupleSize
    }
}

object Validator {

  def apply[A](
      value: A,
      p: A => Boolean,
      errMsg: String,
    )(implicit size: TupleSize[A]
    ): Validator[A] =
    Predicate(value, p, errMsg, size)

  final case class Predicate[A](
      value: A,
      p: A => Boolean,
      msg: String,
      size: TupleSize[A])
      extends Validator[A]

  final case class Both[A, B, C](
      lhs: Validator[A],
      rhs: Validator[B],
      z: Zipper.WithOut[A, B, C])
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
        case Validator.Predicate(v, f, errMsg, _) =>
          if (f(v)) errors else errMsg :: errors

        case both: Validator.Both[a, b, c] =>
          eval(both.lhs, errors) ++ eval(both.rhs, errors)
      }

    private def buildTuple(v: Validator[A]): A =
      v match {
        case Validator.Predicate(v, _, _, _) =>
          v
        case b @ Validator.Both(_, _, _) =>
          val size = b.tupleSize
          println("SIZE: " + size)
          val array: Array[Any] = Array.ofDim(size)
          fillArray(array, v, 0, List.empty)
          arrayToTuple[A](array, size)
      }

    private def fillArray(
        array: Array[Any],
        v: Validator[_],
        ind: Int,
        next: List[Validator[_]],
      ): Array[Any] =
      v match {
        case Both(lhs, rhs, _) =>
          fillArray(array, lhs, ind, rhs :: next)
        case c: Predicate[_] =>
          println(s"$ind - ${c.value}")
          array.update(ind, c.value)
          next match {
            case h :: tail =>
              fillArray(array, h, ind + 1, tail)
            case Nil =>
              array
          }
      }

    private def arrayToTuple[T](array: Array[Any], tupleSize: Int): T =
      // println(s"Converting array ${array.mkString(",")} to tuple of size $tupleSize")
      tupleSize match {
        case 1 => array(0).asInstanceOf[T]
        case 2 => (array(0), array(1)).asInstanceOf[T]
        case 3 => (array(0), array(1), array(2)).asInstanceOf[T]
        case 4 => (array(0), array(1), array(2), array(3)).asInstanceOf[T]
        case 5 => (array(0), array(1), array(2), array(3), array(4)).asInstanceOf[T]
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

  val validator1 =
    row.a.validate("aaa")(_ > -4) ++
      row.b.validate("bbb")(_ == 42.2) ++
      row.c.validate("ccc")(_.startsWith("he")) ++
      row.aOpt.validate("ddd")(_.isDefined) ++
      row.digits.validate("eee")(_.forall(_ > 0))

  println(validator1.run)
}
