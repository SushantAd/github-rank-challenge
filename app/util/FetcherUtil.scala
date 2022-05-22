package util

import play.api.libs.ws.WSResponse

import scala.util.matching.Regex

/**
 * Fetcher Util class for fetching url specific elements and extracting meaningful information - can be extended for other elements
 */

class FetcherUtil {

  private val lastPageRegex: Regex = """[&?]page=(\d+)>; rel="last""".r

/**
* Method to extract Link information from WebService Response
 * Pattern match the last page integer number
 * @params WebService Response
 * @return Option of Int (Value of last page if found)
 */

  def extractPaginatedUrls(response: WSResponse): Option[Int] =
    response.header("Link").flatMap(links => lastPageRegex.findFirstMatchIn(links).map(_.group(1)).map(_.toInt))

}
