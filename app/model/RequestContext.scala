package model

import play.api.mvc.Headers

/**
 * Request Context to carry forward the context of a request coming from the API request to individual services
 * TODO can be extended to be utilised for much more than just headers.
 * @param headers
 */

case class RequestContext(headers: Headers){

  def authorizationToken: Option[String] = headers.get("Authorization")
}

