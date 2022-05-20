package service

import model.OrganizationRepo
import play.api.http.Status
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

class GitHubService @Inject()(ws: WSClient, fetchUtil: FetchUtil){


  def getOrganizationReposDetails(orgName: String)={
   val paginationSizeOpt=  for{
      initialResponse <- wsRequest(UrlBuilder.org(orgName))
      paginatedResultSize = fetchUtil.extractPaginatedUrls(initialResponse)
    } yield paginatedResultSize

    paginationSizeOpt map{
      case Some(nCalls) => {

      }
      case _ =>
    }
  }


  def getOrganizationReposWithPageSize(orgName: String, nCalls: Int)= {

     val repos = (1 to nCalls).foldLeft(Future[List[OrganizationRepo]])((pages, currentPage) =>
        wsRequest(UrlBuilder.orgWithPageNo(orgName, currentPage)).map(_.json.validate[OrganizationRepo].asOpt).map {
          case Some(organizationRepo: OrganizationRepo) => pages :+ organizationRepo
          case _ => List.empty
        }
      )

  }

  def getOrganizationRepoContributors(repo: String): Future[Option[OrganizationRepo]] =
    wsRequest(UrlBuilder.repo(repo)).map(_.json.validate[OrganizationRepo].asOpt)


  def wsRequest(url: String): Future[WSResponse] ={
    ws
      .url(url)
      .get()
      .map{response =>
        response.status match {
          case Status.OK => response
          case _=> throw new Exception("") //todo
        }
      }
  }

}
