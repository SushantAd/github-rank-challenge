package service

import com.typesafe.scalalogging.LazyLogging
import model.{Contributor, RequestContext}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
Concrete class for all functionalities in relation to GitHub Services
@param orgService services all functionalities of a GitHub Organization.
@param repoService services all functionalities of a GitHub Repository.
 */
class GitHubService @Inject()(orgService: OrganizationService, repoService: RepositoryService) extends LazyLogging{

  /**
  Full Load method to get all Contributors - login & contributions for an individual organization.
  @param orgName name of Github Organization.
  @implicit RequestContext
            1) get list of all repos of an organization.
            2) get list of all Contributors [[Contributor]] of all repos.
            3) group By login name -> convert to map -> add all contributions for a single login name
            4) traverse through map and create final list of Contributors
            5) sort based on the number of contributions.
   */
  def getOrganizationContributors(orgName: String)(implicit request: RequestContext): Future[Seq[Contributor]] = {
      logger.info(s"request received to get Organization Contributors Full load for Organization: ${orgName}")
      for {
        orgRepo <- orgService.getOrganizationReposDetails(orgName)
        repoContributors <- repoService.getRepoContributorsFullLoad(orgRepo)
        sortedRepoContributors = {
          repoContributors.groupBy(_.login)
            .mapValues(_.map(_.contributions).sum)
            .map(contributor => Contributor(contributor._1, contributor._2)).toSeq
            .sortBy(_.contributions)(Ordering[Int].reverse)
        }
      } yield sortedRepoContributors
  }.recover{case ex => throw ex}

}
