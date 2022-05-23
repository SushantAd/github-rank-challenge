package helper

import play.api.libs.ws.ahc.AhcWSResponse
import play.api.libs.ws.ahc.cache.{
  CacheableHttpResponseBodyPart,
  CacheableHttpResponseStatus
}
import play.shaded.ahc.io.netty.handler.codec.http.DefaultHttpHeaders
import play.shaded.ahc.org.asynchttpclient.Response
import play.shaded.ahc.org.asynchttpclient.uri.Uri

object CustomTestUtil {

  def createWebResponse(url: String, message: String, statusCode: Int) =
    AhcWSResponse {
      val respBuilder = new Response.ResponseBuilder()
      respBuilder.accumulate(
        new CacheableHttpResponseStatus(
          Uri.create(url),
          statusCode,
          "status text",
          "json"
        )
      )
      respBuilder.accumulate(
        new DefaultHttpHeaders().add("Content-Type", "application/json")
      )
      respBuilder.accumulate(
        new CacheableHttpResponseBodyPart(
          message.getBytes(),
          true
        )
      )
      new AhcWSResponse(respBuilder.build())
    }

  def createWebResponseWithHeader(
      url: String,
      message: String,
      statusCode: Int,
      header: (String, String)
  ) =
    AhcWSResponse {
      val respBuilder = new Response.ResponseBuilder()
      respBuilder.accumulate(
        new CacheableHttpResponseStatus(
          Uri.create(url),
          statusCode,
          "status text",
          "json"
        )
      )
      respBuilder.accumulate(
        new DefaultHttpHeaders()
          .add("Content-Type", "application/json")
          .add(header._1, header._2)
      )
      respBuilder.accumulate(
        new CacheableHttpResponseBodyPart(
          message.getBytes(),
          true
        )
      )
      new AhcWSResponse(respBuilder.build())
    }

}
