package ffscala.misc

def mkFilterArgs(filters: Seq[String]): List[String] =
  val filterlist = processFilters(filters)
  val nonfilters = getNonFilters(filters)
  val filters_v =
    if filterlist(0).length > 0 then
      List("-vf", filterlist(0))
    else
      List()
  val filters_a =
    if filterlist(1).length > 0 then
      List("-af", filterlist(1))
    else
      List()

  filters_v ++ filters_a ++ nonfilters

def processFilters(filters: Seq[String], vf: String = "", af: String = "", i: Int = 0): List[String] =
  val comma =
    if i >= filters.length-2 then
      ""
    else
      ","
  if i >= filters.length then
    List(vf, af)
  else if filters(i) == "v" && i < filters.length-1 then
    processFilters(filters, vf + filters(i+1) + comma, af, i+2)
  else if filters(i) == "a" && i < filters.length-1 then
    processFilters(filters, vf, af + filters(i+1) + comma, i+2)
  else
    processFilters(filters, vf, af, i+1)

def getNonFilters(filters: Seq[String], nf: List[String] = List(), i: Int = 0): List[String] =
  if i >= filters.length then
    nf
  else if filters(i) == "v" || filters(i) == "a" then
    getNonFilters(filters, nf, i+2)
  else
    getNonFilters(filters, nf :+ filters(i), i+1)
