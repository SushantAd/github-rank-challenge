package model

import play.api.libs.json.{Json, OFormat, OWrites}

case class Contributor(login: String, contributions: Int)

object Contributor{
  implicit val contributorFormat: OFormat[Contributor] = Json.format[Contributor]
  implicit val contributorWrite: OWrites[Contributor] = Json.writes[Contributor]
}

