package flight

object FlightApp2 {

  sealed trait Airport {
    def at: Long
  }

  object Airport {
    abstract class AbsAirport(val id: Int) extends Airport

    final case class CHI(at: Long) extends AbsAirport(1)

    final case class ATL(at: Long) extends AbsAirport(2)

    final case class NYK(at: Long) extends AbsAirport(3)

    final case class DAL(at: Long) extends AbsAirport(4)

    final case class CHA(at: Long) extends AbsAirport(5)

    /*def fromString(line: String): Option[Airport] =
      line.trim match {
        case "1" => Some(CHI())
        case "2" => Some(ATL())
        case "3" => Some(NYK())
        case _   => None
      }*/
  }

  sealed trait Itinerary[Origin <: Airport, Dest <: Airport]

  object Itinerary {

    final case class Flight[A <: Airport, B <: Airport](from: A, to: B) extends Itinerary[A, B]

    final case class Both[A <: Airport, B <: Airport, C <: Airport](
      leftLeg: Itinerary[A, B],
      rightLeg: Itinerary[B, C]
    ) extends Itinerary[A, C]

    implicit class ItineraryOps[A <: Airport, B <: Airport](private val self: Itinerary[A, B]) extends AnyVal {
      def ~>[C <: Airport](that: Itinerary[B, C]): Itinerary[A, C] =
        Itinerary.Both(self, that)

      def <~[C <: Airport](that: Itinerary[B, C])(implicit ev: A `IsEqualTo` C): Itinerary[A, C] =
        Itinerary.Both(self, that)
    }

    def flight[From <: Airport, To <: Airport](from: From, to: To)(implicit ev: From ≠ To) =
      Flight[From, To](from, to)

    def draw(it: Itinerary[_, _]): Unit =
      it match {
        case Flight(from, to) =>
          println(s"$from ~> $to")
        case Both(left, right) =>
          draw(left)
          draw(right)
      }
  }

  def main(args: Array[String]): Unit = {

    import Airport.*
    import Itinerary.*

    val a = flight(CHI(1), ATL(2)) ~> flight(ATL(3), NYK(4)) ~> flight(NYK(5), DAL(6)) ~> flight(DAL(7), CHA(8))
    draw(a)

    // flight(CHI(1), CHI(2))

    // flight(CHI(1), ATL(2)) <~ flight(ATL(3), NYK(4))

    println("*************")
  }

}
