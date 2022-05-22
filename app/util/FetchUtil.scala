package service

import play.api.libs.ws.WSResponse

class FetchUtil {

  val lastPageRegex = """[&?]page=(\d+)>; rel="last""".r

  def extractPaginatedUrls(response: WSResponse): Option[Int] = {
    val res =response.header("Link").flatMap(links => lastPageRegex.findFirstMatchIn(links).map(_.group(1)).map(_.toInt))
    println(s"res = ${res}")
    res
  }


}
