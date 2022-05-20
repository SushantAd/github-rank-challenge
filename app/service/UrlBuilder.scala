package service

object UrlBuilder {

  private val baseUrl = s"https://api.github.com" //Todo

  def org(orgName : String): String ={
    s"${baseUrl}/orgs/${orgName}/repos"
  }

  def orgWithPageNo(orgName : String, pageNo: Int): String ={
    s"${org(orgName)}?page=${pageNo}"
  }

  def repo(repoFullName : String)={
    s"${baseUrl}/orgs/${repoFullName}/repos"
  }

  def repoWithPageNo(repoFullName : String, pageNo: Int): String={
    s"${repo(repoFullName)}?page=${pageNo}"
  }

}
