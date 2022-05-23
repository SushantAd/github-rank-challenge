package controllers

import com.typesafe.scalalogging.LazyLogging
import model.RequestContext
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import util.WebServiceUtil

import javax.inject._
import scala.concurrent.ExecutionContext

class HomeController @Inject()(ws: WebServiceUtil, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) with LazyLogging{

  def index() = Action {
    Ok("Welcome to Github Rank Challenge")
  }

  /*
  Only for testing purpose - check your rate limit exhaustion for Rest Calls (Core)
   */
  def rateLimit() = Action.async { implicit  request =>
    logger.info(s"Request received to get rate limit")
    implicit val requestContext = RequestContext(request.headers)
    ws.get("https://api.github.com/rate_limit").map{ response =>
      val core = response.json.\("resources").\("core")
      val reset = core.\("reset").as[Long]
      val nextReset = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss").print(reset*1000L)
      val result = Json.obj("core" -> core.as[JsValue], "next reset date time" -> nextReset)
      Ok(result)
    }
  }

}
