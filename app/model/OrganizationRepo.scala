package model

import play.api.libs.json.{Json, OFormat, OWrites}

case class OrganizationRepo(full_name: String)

case class OrganizationRepos(organizationRepos: List[OrganizationRepos])

object OrganizationRepo{
  implicit val organizationRepoFormat: OFormat[OrganizationRepo] = Json.format[OrganizationRepo]
  implicit val organizationRepoWrite: OWrites[OrganizationRepo] = Json.writes[OrganizationRepo]
}

object OrganizationRepos{
  implicit val organizationReposFormat: OFormat[OrganizationRepos] = Json.format[OrganizationRepos]
  implicit val organizationReposWrite: OWrites[OrganizationRepos] = Json.writes[OrganizationRepos]
}