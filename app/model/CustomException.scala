package model

import play.api.libs.json.{JsString, Json}
import play.api.mvc.Result
import play.api.mvc.Results._


/**
 * An abstract implementation of a Custom Error Handle.
 * Identify domain specific status Code, generate meaningful message and response with appropriate results
 * TODO Can be extended to manage all StatusCodes
 */

trait CustomErrorHandler{

  protected def handleResponse(url: String, message: String, statusCode: Int): Result={
    val response = Json.obj("message" -> JsString(message) , "url" -> url)
    statusCode match {
      case 204 => NoContent
      case 403 => Forbidden(response)
      case 404 => NotFound(response)
      case 500 => InternalServerError(response)
      case _ => InternalServerError(s"Please Contact Admin for below error: ${message} ")
    }
  }
}

case class CustomException(url: String = "Unknown", message: String, statusCode: Int) extends Exception with CustomErrorHandler
{
  def handle = super.handleResponse(url, message, statusCode)
}

case class WebServiceCallException(url: String, message: String, statusCode: Int) extends Throwable with CustomErrorHandler
{
  def handle = super.handleResponse(url, message, statusCode)
}
