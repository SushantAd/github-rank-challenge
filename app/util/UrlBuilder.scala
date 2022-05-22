package util

/**
 * Url Builder helper classes to create appropriate urls based on name
 * TODO baseUrl can be moved to Constants if it will remain the same or Config if there is a chance for change.
 * @param name
 */

case class UrlBuilder(name: String) {

  private val baseUrl = s"https://api.github.com" /*Todo - Should be moved to Constant or Config*/

  def orgUrl: String =
    s"${baseUrl}/orgs/${name}/repos?per_page=100"

  def repoUrl: String =
    s"${baseUrl}/repos/${name}/contributors?per_page=100"

  def orgWithPageNoUrl(currentPageNo: Int): String =
    s"${UrlBuilder(name).orgUrl}&page=${currentPageNo}"

  def repoWithPageNoUrl(currentPageNo: Int): String =
    s"${UrlBuilder(name).repoUrl}&page=${currentPageNo}"

}
