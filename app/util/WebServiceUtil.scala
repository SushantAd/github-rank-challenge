package util

import com.typesafe.scalalogging.LazyLogging
import model.{RequestContext, WebServiceCallException}
import play.api.http.Status
import play.api.libs.ws.{WSClient, WSResponse}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Utility class to for making WS calls to required URLs - can be extended for other ws calls
 * @param ws webservice client - packaged with Play framework
 */

class WebServiceUtil @Inject()(ws: WSClient) extends LazyLogging{

  /**
   * List of header values, that should be passed with all requests
   * TODO Can be moved to config
   */
  private val defaultHeaders: List[(String, String)] = List("User-Agent" -> "GitHubRank", "Accept" -> "application/vnd.github.v3+json")

/**
  * Method to make web service-get calls with predefined set of error responses to be taken care of -TODO can be extended to handle all error responses
 *@param url web url to be called
 *@return List of WSResponse
**/

  def get(url: String)(implicit request: RequestContext): Future[WSResponse] = {
    Try {
      ws
        .url(url)
        .withHttpHeaders(HeaderValues: _*)
        .get()
        .map { response =>
          response.status match {
            case i if i == Status.OK => response
            case i if i == Status.NO_CONTENT => logger.error("No Content for Url");throw WebServiceCallException(url, "No content", i)
            case i if i == Status.NOT_FOUND =>  logger.error(response.body); throw WebServiceCallException(url,response.json.\("message").as[String], i)
            case i if i == Status.FORBIDDEN =>  logger.error(response.body); throw WebServiceCallException(url,response.json.\("message").as[String], i)
            case _ => logger.error(response.body.lift.toString()); throw WebServiceCallException(url,response.body.lift.toString(), Status.INTERNAL_SERVER_ERROR)
          }
        }
    } match {
      case Success(value) => value
      case Failure(exception) => logger.info(exception.getMessage,exception); throw exception
    }
  }

  /** TODO - Can be extended to get token type from request context and generate Authorization header accordingly
   */
  private def HeaderValues(implicit requestContext: RequestContext): List[(String, String)] ={
    (Try(sys.env("GH_TOKEN")).toOption,requestContext.authorizationToken) match {
      case (Some(ghToken), Some(token)) => defaultHeaders ++ List(("Authorization", s"token ${ghToken}"))
      case (None, Some(token)) => defaultHeaders ++ List(("Authorization", s"token ${token}"))
      case (Some(ghToken), None) => defaultHeaders ++ List(("Authorization", s"token ${ghToken}"))
      case _ => defaultHeaders
    }
  }
}
