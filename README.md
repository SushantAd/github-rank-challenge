# Scala GitHub Rank Challenge using Play

Problem Statement:
GitHub portal is centered around organizations and repositories. Each organization has many
repositories and each repository has many contributors. Your goal is to create an endpoint that given
the name of the organization will return a list of contributors sorted by the number of contributions.

Few Other Requirements:
1. respond to a GET request at port 8080 and address /org/{org_name}/contributors\.
   - Change default port in build.sbt   PlayKeys.playDefaultPort := 8080
2. handle GitHub’s API rate limit restriction using a token that can be set as an environment variable of name GH_TOKEN.
   - Set GH_Token as env variable or pass token in header (with key - Authorization) (Token value currently used is GitHub Personal token - can be extended later)
4. GitHub API returns paginated responses. You should take it into account if you want the result to be accurate for larger organizations.
   - We make 1 extra call to fetch number of pages and then async-iterate through all pages from 1 to max page number.
   - We also fetch the maximum records possible for a single api call (i.e page_size=100)

Project:
The project is build on Play framework to utilize the efficiency in creating/consuming API's and allowing smooth plug and play access of Play supported libraries.

Libraries:
1. scala-logging     -> To utilize lazy logging 
2. ehcache-jcache    -> For API caching
3. play ws           -> WebService Client
4. salatestplus-play -> For Unit Testing
5. mockito-scala     -> For mock objects

## Appendix

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, type/run the following command in the terminal, this will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at <http://localhost:8080/>. You don't need to deploy or reload anything -- changing
any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

### Usage

Current Possible Routes

```routes
GET     /                               

GET     /rate/limit                     

GET     /org/:orgName/contributors      
```

Route definitions:

```routes
GET     / 
```
Index route -> Starts when application is run -> "Welcome to Github Rank Challenge"

```routes
GET     /rate/limit
```

Utility API created only for testing purpose, to identify number of API requests still available in rate limit
and also the next rate limit refresh date time.

Note: Test using 'Authorization <git_personal_token>' in header or GH_TOKEN as env_variable.

Response sample:
```Response
{
    "core": {
        "limit": 5000,
        "used": 0,
        "remaining": 5000,
        "reset": 1653335771
    },
    "next reset date time": "2022-05-24 01:26:11"
}
```

```routes
GET     /org/:orgName/contributors      
```

Fetch Array of contributors of a specific organization, sorted based on number of contributions made to all repositories worked on.

Request : orgName in url

Header: Optional Authorization header.

Response sample:

example url: localhost:8080/org/io/contributors
```Response
[
    {
        "login": "jgilliam",
        "contributions": 409
    },
    {
        "login": "rbjarnason",
        "contributions": 347
    },
    {
        "login": "fannar",
        "contributions": 83
    },
    {
        "login": "aldavigdis",
        "contributions": 58
    },
    {
        "login": "simmix1",
        "contributions": 8
    }
]
```

### Note: API is currently cached for 2 minutes, can be increased/decreased as needed.


### Application Flow
Request => Routes => Controller => Cache => Service

1. GithubService calls OrgServices to fetch list of all repos in an Organization.
2. List of repos is passed to RepositoryService to make sequential async calls to all repos to fetch list of Contributors.
3. List of contributors are than grouped by login and their contributions are added.
4. The final list is then sorted based on Contribution value of each login.
5. Final result is responded back to the called.
6. Error are handled with appropriate error codes (disclaimer: Not all http status codes are managed at present)

### Improvements to be added
1. Integration tests. 
2. Configuration for different environments.
3. Constant file to be added.

### Edge and Extensions to be noted
1. At the present any error while fetching the api data returns an error response, this can be further extended to respond
with a list of both contributions and list of errors for each individual url called.

Example: localhost:8080/org/zio/contributors

Response: 204 No Content. (This was found while API - testing) 1 of the repos in ZIO does not have any content. Hence, 204 response.

2. We can ignore urls having errors, at the moment we don't.

### Note
I have added TODO in multiple parts of the program, listing possible extensions and enhancement that can be added.
