package controllers

import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

class HomeController @Inject()(cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action {
    Ok("Welcome to Github Rank Challenge")
  }

}
