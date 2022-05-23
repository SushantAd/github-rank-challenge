package service

import helper.CustomTestUtil
import model.{Contributor, OrganizationRepo, RequestContext}
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcWSResponse
import play.api.mvc._
import play.api.test._
import util.{FetcherUtil, UrlBuilder, WebServiceUtil}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class RepositoryServiceSpec extends PlaySpec with MockitoSugar {
  val webUtilMock = mock[WebServiceUtil]
  val fetcherUtilMock = mock[FetcherUtil]

  val repoService = new RepositoryService(webUtilMock, fetcherUtilMock)

  val repoName1 = "testRepo1"
  val repoName2 = "testRepo2"
  implicit val requestContext: RequestContext = RequestContext(
    Headers("Authorization" -> "abc")
  )
  val contributors1 = Seq(Contributor("login1", 10), Contributor("login2", 20))
  val contributors2 = Seq(Contributor("login3", 30), Contributor("login4", 40))

  val orgRepoSeq =
    Seq(OrganizationRepo("testRepo1"), OrganizationRepo("testRepo2"))

  val repoUrlBuilder = UrlBuilder(_)
  val jsonWebResponse1: String = Json
    .arr(
      Json.obj("login" -> "login1", "contributions" -> 10),
      Json.obj("login" -> "login2", "contributions" -> 20)
    )
    .toString()

  val jsonWebResponse2: String = Json
    .arr(
      Json.obj("login" -> "login3", "contributions" -> 30),
      Json.obj("login" -> "login4", "contributions" -> 40)
    )
    .toString()

  "RepositoryService" should {
    "get Repo Contributors FullLoad for valid list of repos" in {
//First repo call
      val webResponse1: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          repoUrlBuilder(repoName1).repoUrl,
          jsonWebResponse1,
          200
        )

      when(
        webUtilMock.get(repoUrlBuilder(repoName1).repoUrl)
      ) thenReturn Future.successful(webResponse1)
      when(
        fetcherUtilMock.extractPaginatedUrls(webResponse1)
      ) thenReturn None

      when(
        webUtilMock.get(repoUrlBuilder(repoName1).repoWithPageNoUrl(1))
      ) thenReturn Future.successful(webResponse1)

      //Second Repo call
      val webResponse2: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          repoUrlBuilder(repoName2).repoUrl,
          jsonWebResponse2,
          200
        )

      when(
        webUtilMock.get(repoUrlBuilder(repoName2).repoUrl)
      ) thenReturn Future.successful(webResponse2)
      when(
        fetcherUtilMock.extractPaginatedUrls(webResponse2)
      ) thenReturn None

      when(
        webUtilMock.get(repoUrlBuilder(repoName2).repoWithPageNoUrl(1))
      ) thenReturn Future.successful(webResponse2)

      val result = repoService.getRepoContributorsFullLoad(orgRepoSeq)
      result.map(res => res mustBe contributors1)
    }

    "get Organization ReposDetails for valid repoName for multiple pages" in {
      val webResponse1: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          repoUrlBuilder(repoName2).repoUrl,
          jsonWebResponse1,
          200
        )
      val webResponse2: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          repoUrlBuilder(repoName2).repoUrl,
          jsonWebResponse2,
          200
        )

      when(
        webUtilMock.get(repoUrlBuilder(repoName1).repoUrl)
      ) thenReturn Future.successful(webResponse1)

      when(
        fetcherUtilMock.extractPaginatedUrls(webResponse1)
      ) thenReturn Some(2)

      when(
        webUtilMock.get(repoUrlBuilder(repoName1).repoWithPageNoUrl(1))
      ) thenReturn Future.successful(webResponse1)

      when(
        webUtilMock.get(repoUrlBuilder(repoName1).repoWithPageNoUrl(2))
      ) thenReturn Future.successful(webResponse2)

      val result = Await.result(
        repoService.getRepoContributorsFullLoad(Seq(orgRepoSeq.head)),
        2.minute
      )
      result mustBe contributors1 ++ contributors2
    }

    "Throw exception for invalid web service response" in {
      val webResponse: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          repoUrlBuilder(repoName1).repoUrl,
          jsonWebResponse1,
          400
        )

      when(
        webUtilMock.get(repoUrlBuilder(repoName1).repoUrl)
      ) thenThrow new Exception("something went wrong")

      val result = intercept[Exception] {
        repoService.getRepoContributorsFullLoad(orgRepoSeq)
      }

      result.getMessage mustBe ("something went wrong")
    }
  }
}
