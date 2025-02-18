package object validation {

  final case class DbRow(
      a: Int,
      b: Double,
      c: String,
      aOpt: Option[Int],
      digits: List[Int])

}
