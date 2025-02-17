package flight

sealed trait Airport2 {
  def id: Long
}

object Airport2 {
  abstract class AbsAirport(val id: Long) extends Airport2

  final case class CHI() extends AbsAirport(1)

  final case class ATL() extends AbsAirport(2)

  final case class NYK() extends AbsAirport(3)

  final case class DAL() extends AbsAirport(4)

  final case class CHA() extends AbsAirport(5)

  /*def fromString(line: String): Option[Airport] =
    line.trim match {
      case "1" => Some(CHI())
      case "2" => Some(ATL())
      case "3" => Some(NYK())
      case _   => None
    }*/
}

sealed trait Itinerary2[Origin <: Airport2, Dest <: Airport2]

object Itinerary2 {

  final case class Flight[A <: Airport2, B <: Airport2](from: A, to: B) extends Itinerary2[A, B]

  final case class Both[A <: Airport2, B <: Airport2, C <: Airport2](
      leftLeg: Itinerary2[A, B],
      rightLeg: Itinerary2[B, C])
      extends Itinerary2[A, C]

  implicit class ItineraryOps[A <: Airport2, B <: Airport2](private val self: Itinerary2[A, B]) extends AnyVal {
    def ~>[C <: Airport2](that: Itinerary2[B, C]): Itinerary2[A, C] =
      Itinerary2.Both[A, B, C](self, that)

    def <~[C <: Airport2](that: Itinerary2[B, C])(implicit ev: A `IsEqualTo` C): Itinerary2[A, C] =
      Itinerary2.Both[A, B, C](self, that)
  }

  def flight[From <: Airport2, To <: Airport2](from: From, to: To)(implicit ev: From ≠ To): Itinerary2[From, To] =
    Flight[From, To](from, to)

  def draw(it: Itinerary2[?, ?]): Unit =
    it match {
      case Flight(from, to) =>
        println(s"$from ~> $to")
      case Both(left, right) =>
        draw(left)
        draw(right)
    }
}

object FlightApp2 {

  def main(args: Array[String]): Unit = {

    import Airport2.*
    import Itinerary2.*

    val a = flight(CHI(), ATL()) ~> flight(ATL(), NYK()) ~> flight(NYK(), DAL()) ~> flight(DAL(), CHA())
    draw(a)

    println("2 leg flight")
    val backAndForth = flight(CHI(), ATL()) <~ flight(ATL(), CHI())
    draw(backAndForth)

    // flight(CHI(), CHI())
    // flight(CHI(), ATL()) <~ flight(ATL(), NYK())

  }
}
