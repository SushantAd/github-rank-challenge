package service

import com.typesafe.scalalogging.LazyLogging
import model.{CustomException, OrganizationRepo, RequestContext, WebServiceCallException}
import play.api.http.Status
import util.{FetcherUtil, UrlBuilder, WebServiceUtil}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
Concrete class for all functionalities in relation to GitHub Organization
@param WebServiceUtil communicates with other web services.
@param fetcherUtil fetches extracted information from Webservice responses.
 */

class OrganizationService @Inject()(ws: WebServiceUtil, fetchUtil: FetcherUtil) extends LazyLogging{

  /**
  Single Load method to get all OrganizationRepo - full_name for a single repository.
  @param orgName name of individual organization.
  @implicit RequestContext.
   1) make 1 extra call to orgUrl to fetch the number of pages for repositories.
   2) fetch and extract page size from Ws Response Header.
   3) make n number of calls based on page size.
   */

  def getOrganizationReposDetails(orgName: String)(implicit request: RequestContext): Future[Seq[OrganizationRepo]] ={
    logger.info(s"request received to get Organization Repos for Organization: ${orgName}")
    val urlBuilder = UrlBuilder(orgName)
    for{
      initialResponse <- ws.get(urlBuilder.orgUrl)
      paginatedResultSize = fetchUtil.extractPaginatedUrls(initialResponse)
      n = paginatedResultSize.getOrElse(1) //default size of 1
      res <- getOrganizationReposWithPageSize(urlBuilder, n).map(_.distinct)
    } yield res
  }.recover{case ex => throw ex}

  /**
  Helper method to make multiple webservice calls based on the number of pages for a single organization.
  @param urlBuilder UrlBuilder object to get proper Urls created from orgNames.
  @implicit RequestContext
   */

  private def getOrganizationReposWithPageSize(urlBuilder: UrlBuilder, pages: Int)(implicit request: RequestContext): Future[Seq[OrganizationRepo]]= {
    Try{
      Future.sequence{
        (1 to pages)
          .map{pageNo => ws.get(urlBuilder.orgWithPageNoUrl(pageNo))
            .map(_.json.as[List[OrganizationRepo]])}
      }} match {
      case Success(value)=> value.map(_.flatten)
      case Failure(exception) =>  exception match {
        case ex: WebServiceCallException => throw ex
        case ex => throw CustomException(urlBuilder.orgUrl, ex.getMessage, Status.INTERNAL_SERVER_ERROR)
      }
    }
  }
}
