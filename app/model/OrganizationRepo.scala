package model

import play.api.libs.json.{Format, Json, Writes}

case class OrganizationRepo(full_name: String)

object OrganizationRepo{
  implicit val organizationRepoFormat: Format[OrganizationRepo] = Json.format[OrganizationRepo]
  implicit val organizationRepoWrite: Writes[OrganizationRepo] = Json.writes[OrganizationRepo]
}
