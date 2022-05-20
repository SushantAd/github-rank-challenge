package controllers

import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class GitHubRankController @Inject()(cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getContributors(orgName: String): Action[AnyContent] = Action.async {
    //Todo
    Future(Ok("Welcome to Github Rank Challenge"))
  }

}
