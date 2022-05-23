package util

import helper.CustomTestUtil
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class FetcherUtilSpec extends PlaySpec with MockitoSugar {

  val fetcherUtil = new FetcherUtil()

  "FetcherUtilSpec" should {
    "get some pageSize for valid webResponse header" in {
      val link = (
        "Link",
        "<https://api.github.com/organizations/49655448/repos?page=2>; rel=\"next\", <https://api.github.com/organizations/49655448/repos?page=3>; rel=\"last\""
      )
      val wsResponse =
        CustomTestUtil.createWebResponseWithHeader(
          UrlBuilder("org1").orgUrl,
          "message",
          200,
          link
        )

      fetcherUtil.extractPaginatedUrls(wsResponse) mustBe Some(3)

    }

    "get None for valid webResponse header" in {
      val wsResponse = CustomTestUtil.createWebResponse(
        UrlBuilder("org1").orgUrl,
        "message",
        200
      )

      fetcherUtil.extractPaginatedUrls(wsResponse) mustBe None
    }
  }
}
