package service

import model.{Contributor, OrganizationRepo, RequestContext}
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class GitHubServiceSpec extends PlaySpec with MockitoSugar {
  val orgServiceMock = mock[OrganizationService]
  val repoServiceMock = mock[RepositoryService]

  val githubService = new GitHubService(orgServiceMock, repoServiceMock)

  val orgName = "testOrganization"
  implicit val requestContext = RequestContext(
    Headers("Authorization" -> "abc")
  )
  val orgRepos = Seq(OrganizationRepo("org1"), OrganizationRepo("org2"))
  val contributors =
    Seq(Contributor("test user1", 500), Contributor("test user2", 250))

  "GitHubService" should {
    "get Organization Contributors for valid orgName" in {

      when(
        orgServiceMock.getOrganizationReposDetails(orgName)
      ) thenReturn Future.successful(orgRepos)
      when(
        repoServiceMock.getRepoContributorsFullLoad(orgRepos)
      ) thenReturn Future.successful(contributors)

      val result = Await.result(
        githubService.getOrganizationContributors(orgName),
        2.minute
      )
      result mustBe contributors
    }

    "get sorted Organization Contributors for valid orgName" in {
      val unsortedContributors = contributors :+ Contributor("test user3", 1000)
      val sortedContributors =
        unsortedContributors.sortBy(_.contributions)(Ordering[Int].reverse)

      when(
        orgServiceMock.getOrganizationReposDetails(orgName)
      ) thenReturn Future.successful(orgRepos)
      when(
        repoServiceMock.getRepoContributorsFullLoad(orgRepos)
      ) thenReturn Future.successful(unsortedContributors)

      val result = Await.result(
        githubService.getOrganizationContributors(orgName),
        2.minute
      )
      result mustBe sortedContributors
    }

    "Throw exception for invalid request" in {
      when(
        orgServiceMock.getOrganizationReposDetails(orgName)
      ) thenThrow new Exception("something went wrong")

      val result = intercept[Exception] {
        githubService.getOrganizationContributors(orgName)
      }

      result.getMessage mustBe ("something went wrong")
    }
  }
}
