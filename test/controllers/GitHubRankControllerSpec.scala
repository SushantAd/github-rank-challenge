//package controllers
//
//import model.{Contributor, RequestContext}
//import org.mockito.MockitoSugar
//import org.scalatestplus.play.PlaySpec
//import play.api.cache.AsyncCacheApi
//import play.api.mvc._
//import play.api.test._
//import play.api.test.Helpers.{GET, contentAsString, defaultAwaitTimeout}
//import service.GitHubService
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import scala.concurrent.duration.DurationInt
//
//class GitHubRankControllerSpec extends PlaySpec with MockitoSugar {
//
//  val gitHubServiceMock = mock[GitHubService]
//  val cacheMock = mock[AsyncCacheApi]
//
//  val controller = new GitHubRankController(
//    Helpers.stubControllerComponents(),
//    gitHubServiceMock,
//    cacheMock
//  )
//  implicit val requestContext = RequestContext(
//    Headers("Authorization" -> "abc")
//  )
//  val orgName = "testOrganization"
//  val contributors =
//    Seq(Contributor("test user1", 500), Contributor("test user2", 250))
//
//  "GitHubRankController" should {
//    "getContributors should be valid with 200 response" in {
//      val request = FakeRequest(GET, s"/org/${orgName}/contributors")
//        .withHeaders(("Host", "localhost"), "Authorization" -> "abc")
////      when(
////        gitHubServiceMock.getOrganizationContributors(orgName)
////      ) thenReturn Future(contributors)
//      when(
//        cacheMock.getOrElseUpdate(orgName, 2.minutes)(
//          gitHubServiceMock.getOrganizationContributors(orgName)
//        )
//      ) thenReturn Future(contributors)
//
//      val result: Future[Result] =
//        controller.getContributors(orgName).apply(request)
//      val bodyText: String = contentAsString(result)
//      bodyText mustBe "ok"
//    }
//  }
//}
