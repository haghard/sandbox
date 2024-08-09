package flight

import scala.reflect.ClassTag
import Airport.*

sealed trait Airport

object Airport {
  // abstract - you need a sub type in order to instantiate it
  // final - you can't subtype
  final abstract class NYK extends Airport
  final abstract class LAL extends Airport
  final abstract class DEN extends Airport
}

sealed trait Itinerary[Src <: Airport, Dest <: Airport]

object Itinerary {

  final case class Flight[A <: Airport: ClassTag, B <: Airport: ClassTag](
      departureTs: Long,
      arrivalTs: Long)
      extends Itinerary[A, B] {

    override val toString: String = {
      val from = implicitly[ClassTag[A]].runtimeClass.getSimpleName
      val to = implicitly[ClassTag[B]].runtimeClass.getSimpleName
      s"$from($departureTs) ~> $to($arrivalTs)"
    }
  }

  final case class Both[A <: Airport, B <: Airport, C <: Airport](
      left: Itinerary[A, B],
      right: Itinerary[B, C])
      extends Itinerary[A, C]

  implicit class ItineraryOps[A <: Airport, B <: Airport](val self: Itinerary[A, B]) extends AnyVal {

    def ~>[C <: Airport](that: Itinerary[B, C]): Itinerary[A, C] =
      Itinerary.Both(self, that)

    def <~[C <: Airport](that: Itinerary[B, C])(implicit ev: A `IsEqualTo` C): Itinerary[A, C] =
      Itinerary.Both(self, that)
  }

  def flight[From <: Airport: ClassTag, To <: Airport: ClassTag](
      departureTs: Long,
      arrivalTs: Long,
    )(implicit ev: From `≠` To
    ) =
    Flight[From, To](departureTs, arrivalTs) // If From ≠ To, the ev is generated. Otherwise,

  /*def match0[From <: Airport, To <: Airport](it: Itinerary[From, To]): Int =
    it match {
      case _: Flight[from, to] => 1
      //case _                   => 0
    }*/

  def draw0(it: Itinerary[?, ?]): Unit =
    it match {
      case f @ Flight(_, _) =>
        println(f.toString)
      case Both(left, right) =>
        draw0(left)
        draw0(right)
    }

  def draw(it: Itinerary[?, ?]): Unit =
    it match {
      case f: Flight[from, to] =>
        println(f.toString)
      case b: Both[from, via, to] =>
        implicitly[from `≠` to]
        draw(b.left)
        draw(b.right)
    }

  def drawFromNYK(it: Itinerary[NYK, ?]): Unit =
    it match {
      case f: Flight[from, to] =>
        println(f.toString)
      case b: Both[from, via, to] =>
        implicitly[from `≠` to]
        draw(b.left)
        draw(b.right)
    }
}

object FlightApp {
  import Airport.*

  def main(args: Array[String]): Unit = {

    import Itinerary.*

    val a = flight[NYK, DEN](1, 2) ~> flight[DEN, LAL](3, 5)
    draw(a)

    // drawFromNYK(flight[DEN, NYK](1, 2))

    val b = flight[NYK, DEN](1, 2) <~ flight[DEN, NYK](3, 5)
    draw(b)

    // constraint violation: Actual flight.FlightApp.LAL but expected flight.FlightApp.NYK
    // flight[NYK, DEN](1, 2) <~ flight[DEN, LAL](3, 5)

    // constraint violation: A flight cannot start at flight.FlightApp.NYK and stop at flight.FlightApp.NYK
    // flight[NYK, NYK](1, 2)

    println("*********")
  }
}

/*
Another example:

Shipment
 Pickup_Stop(InTransit->Arrived->Departed)
 Delivery_Stop(InTransit->Arrived->Departed)
 */
