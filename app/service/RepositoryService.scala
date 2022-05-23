package service

import com.typesafe.scalalogging.LazyLogging
import model._
import play.api.http.Status
import util.{FetcherUtil, UrlBuilder, WebServiceUtil}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
Concrete class for all functionalities in relation to GitHub repositories
@param WebServiceUtil communicates with other web services.
@param fetcherUtil fetches extracted information from Webservice responses.
 */

class RepositoryService @Inject()(ws: WebServiceUtil, fetcherUtil: FetcherUtil) extends LazyLogging{

  /**
  Full Load method to get all Contributors - login & contributions for a list of repositories of an organization.
  @param repos List of Organization repositories [[OrganizationRepo]]
  @implicit RequestContext
   1) make sequential async calls based on organization repositories list.
   */

  def getRepoContributorsFullLoad(repos: Seq[OrganizationRepo])(implicit request: RequestContext): Future[Seq[Contributor]] = {
    logger.info(s"Received request for contributors full load")
    Future.sequence(repos.map(repo => getRepoContributors(repo.full_name))).map(_.flatten)
  }.recover{case ex => throw ex}

  /**
  Single Load method to get all Contributors - login & contributions for a single repository.
  @param repoName name of individual repository
  @implicit RequestContext
   1) make 1 extra call to orgUrl to fetch the number of pages for contributions of individual repositories.
   2) fetch and extract page size from Ws Response Header.
   3) make n number of calls based on page size.
   */

  private def getRepoContributors(repoName: String)(implicit request: RequestContext): Future[Seq[Contributor]] ={
    val urlBuilder = UrlBuilder(repoName)
    for{
      initialResponse <- ws.get(urlBuilder.repoUrl)
      paginatedResultSize = fetcherUtil.extractPaginatedUrls(initialResponse)
      pages = paginatedResultSize.getOrElse(1) //default size of 1
      result <- getRepoContributionsWithPageSize(urlBuilder, pages).map(_.distinct)
    } yield result
  }.recover{case ex => throw ex}

  /**
  Helper method to make multiple webservice calls based on the number of pages for a single repository.
  @param urlBuilder UrlBuilder object to get proper Urls created from repoNames
  @implicit RequestContext
   */

  private def getRepoContributionsWithPageSize(urlBuilder: UrlBuilder, pages: Int)(implicit request: RequestContext): Future[Seq[Contributor]]= {
    Try{
      Future.sequence{
        (1 to pages)
          .map{pageNo => ws.get(urlBuilder.repoWithPageNoUrl(pageNo))
            .map(_.json.as[List[Contributor]])}
      }} match {
      case Success(value)=> value.map(_.flatten)
      case Failure(exception) =>  exception match {
        case ex: WebServiceCallException => throw ex
        case ex =>  throw CustomException(urlBuilder.repoUrl, ex.getMessage, Status.INTERNAL_SERVER_ERROR)
      }
    }
  }
}
