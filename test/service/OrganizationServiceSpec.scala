package service

import com.gargoylesoftware.htmlunit.{WebRequest, WebResponse}
import helper.CustomTestUtil
import model.{Contributor, OrganizationRepo, RequestContext}
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcWSResponse
import play.api.mvc._
import play.api.test.WsTestClient
import play.libs.ws.WSResponse
import util.{FetcherUtil, UrlBuilder, WebServiceUtil}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class OrganizationServiceSpec extends PlaySpec with MockitoSugar {
  val webUtilMock = mock[WebServiceUtil]
  val fetcherUtilMock = mock[FetcherUtil]

  val organizationService =
    new OrganizationService(webUtilMock, fetcherUtilMock)

  val orgName = "testOrganization"
  implicit val requestContext: RequestContext = RequestContext(
    Headers("Authorization" -> "abc")
  )
  val orgRepos1 = Seq(OrganizationRepo("org1"), OrganizationRepo("org2"))
  val orgRepos2 = Seq(OrganizationRepo("org3"), OrganizationRepo("org4"))

  val orgUrlBuilder: UrlBuilder = UrlBuilder(orgName)
  val jsonWebResponse: String = Json
    .arr(Json.obj("full_name" -> "org1"), Json.obj("full_name" -> "org2"))
    .toString()

  val jsonWebResponse2: String = Json
    .arr(Json.obj("full_name" -> "org3"), Json.obj("full_name" -> "org4"))
    .toString()

  "OrganizationService" should {
    "get Organization ReposDetails for valid orgName" in {
      val webResponse: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          orgUrlBuilder.orgUrl,
          jsonWebResponse,
          200
        )

      when(
        webUtilMock.get(orgUrlBuilder.orgUrl)
      ) thenReturn Future.successful(webResponse)
      when(
        fetcherUtilMock.extractPaginatedUrls(webResponse)
      ) thenReturn None

      when(
        webUtilMock.get(orgUrlBuilder.orgWithPageNoUrl(1))
      ) thenReturn Future.successful(webResponse)

      val result = Await.result(
        organizationService.getOrganizationReposDetails(orgName),
        2.minute
      )
      result mustBe orgRepos1
    }

    "get Organization ReposDetails for valid orgName for multiple pages" in {
      val webResponse1: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          orgUrlBuilder.orgUrl,
          jsonWebResponse,
          200
        )

      val webResponse2: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          orgUrlBuilder.orgUrl,
          jsonWebResponse2,
          200
        )

      when(
        webUtilMock.get(orgUrlBuilder.orgUrl)
      ) thenReturn Future.successful(webResponse1)
      when(
        fetcherUtilMock.extractPaginatedUrls(webResponse1)
      ) thenReturn Some(2)

      when(
        webUtilMock.get(orgUrlBuilder.orgWithPageNoUrl(1))
      ) thenReturn Future.successful(webResponse1)

      when(
        webUtilMock.get(orgUrlBuilder.orgWithPageNoUrl(2))
      ) thenReturn Future.successful(webResponse2)

      val result = Await.result(
        organizationService.getOrganizationReposDetails(orgName),
        2.minute
      )
      result mustBe (orgRepos1 ++ orgRepos2)
    }

    "Throw exception for invalid web service response" in {
      val webResponse: AhcWSResponse =
        CustomTestUtil.createWebResponse(
          orgUrlBuilder.orgUrl,
          jsonWebResponse,
          400
        )

      when(
        webUtilMock.get(orgUrlBuilder.orgUrl)
      ) thenThrow new Exception("something went wrong")

      val result = intercept[Exception] {
        organizationService.getOrganizationReposDetails(orgName)
      }

      result.getMessage mustBe ("something went wrong")
    }
  }
}
