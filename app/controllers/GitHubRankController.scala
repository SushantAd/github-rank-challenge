package controllers

import com.typesafe.scalalogging.LazyLogging
import model.{Contributor, CustomException, RequestContext, WebServiceCallException}
import play.api.cache.AsyncCacheApi
import play.api.libs.json.Json
import play.api.mvc._
import service.GitHubService

import javax.inject._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class GitHubRankController @Inject()(cc: ControllerComponents, gitHubService: GitHubService, cache: AsyncCacheApi)(implicit exec: ExecutionContext) extends AbstractController(cc) with LazyLogging{

  def getContributors(orgName: String): Action[AnyContent] = Action.async {implicit request =>
    {
      logger.info(s"Request received to get Organization Contributors for ${orgName}")
      implicit val requestContext: RequestContext = RequestContext(request.headers)
      cache.getOrElseUpdate[Seq[Contributor]](orgName, 2.minutes)(gitHubService.getOrganizationContributors(orgName))
        .map(result =>Ok(Json.toJson(result)))
    }.recover{
      case ex: WebServiceCallException => ex.handle
      case ex: CustomException => ex.handle
      case ex => CustomException(message = ex.getMessage, statusCode =  500).handle
    }
  }

}
