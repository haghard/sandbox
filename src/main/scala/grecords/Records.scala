package grecords

import izumi.reflect.Tag

//https://youtu.be/48fpPffgnMo?list=LL
//https://stackoverflow.com/questions/12135293/type-constraint-for-type-inequality-in-scala

//Type constraint for type inequality in scala
sealed trait NotContains[A, B]
object NotContains {
  implicit def nsub[A, B]: A `NotContains` B = new NotContains[A, B] {}

  @scala.annotation.implicitAmbiguous("Column constraint violation: Couldn't add ${B} type twice in [${A}] !")
  implicit def nsubAmbig1[A, B >: A]: A `NotContains` B = sys.error("Unexpected call")
  implicit def nsubAmbig2[A, B >: A]: A `NotContains` B = sys.error("Unexpected call")
}

@scala.annotation.implicitNotFound("Column constraint violation: Couldn't find ${Out} in [${In}] !")
sealed trait Contains[-In, +Out]
object Contains {
  implicit def nsub[A]: Contains[A, A] =
    new Contains[A, A] {}
}

final case class Column[+T](private val map: Map[Tag[?], Any])

object Column {
  implicit class HSetOps[C <: Column[?]](self: C) {
    // |+|
    def ++[A <: Column[?]](that: A)(implicit ev: C `NotContains` A): C & A =
      new Column(self.map + that.map.head).asInstanceOf[C & A]

    def get[A: Tag](implicit ev: C `Contains` Column[A]): A =
      self.map(implicitly[Tag[A]]).asInstanceOf[A]
  }

  def apply[A: Tag](a: A): Column[A] =
    new Column(Map(implicitly[Tag[A]] -> a))
}

object Program {
  import Column.*

  def main(args: Array[String]): Unit = {

    val columns =
      Column(1) ++ Column("a") ++ Column(true) ++ Column(List(1, 2)) ++ Column(List("1", "2")) // ++ Column(false)

    /*
    implicitly[Column[Int] with Column[String] with Column[Boolean] <:< Column[Boolean]]
    implicitly[Column[Int] with Column[String] with Column[Boolean] <:< Column[String]]
    implicitly[Column[Int] with Column[String] with Column[Boolean] <:< Column[Int]]
    implicitly[java.lang.Long <:< Number]
     */

    columns.get[Int]
    columns.get[Boolean]

    columns.get[List[Int]]
    columns.get[List[String]]

    // columns.get[Nothing]

    // Column constraint violation: Cannot find grecords.Column[Byte] in [grecords.Column[Int] with grecords.Column[String] with grecords.Column[Boolean] with grecords.Column[List[Int]] with grecords.Column[List[String]]]
    // columns.get[Byte]
  }
}
