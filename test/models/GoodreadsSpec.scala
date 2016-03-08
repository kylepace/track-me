package models

import controllers.{GoodReadsUser, ApiAuth}
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.libs.oauth.{RequestToken, OAuthCalculator}
import play.api.libs.ws.WS
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.xml.XML

@RunWith(classOf[JUnitRunner])
class GoodReadsSpec extends Specification {

  val testTokenKey = "NVrj9NZFL6vxuHKjBLBa9Q"
  val testTokenSecret = "UbYbiOMj6fWYuFUaMFpeC8LyFQfcWYk8MVg8x6ItgoA"
  val requestToken = RequestToken(testTokenKey, testTokenSecret)

  "GoodReads current user" should {
    "return current user id" in new WithApplication {
      val apiAuth = new ApiAuth()
      val uri = "https://www.goodreads.com/api/auth_user"

      val request = WS.url(uri).sign(OAuthCalculator(apiAuth.KEY, requestToken))

      val result = Await.result(request.get, 5 seconds)
      result.status must be equalTo(200)

      // move this stuff out to a separate class
      val xmlDoc = XML.loadString(result.body)
      val userId = (xmlDoc \ "user").\@("id")
      val name = (xmlDoc \ "name").text
      val url = (xmlDoc \ "link").text
      val goodReadsUser = GoodReadsUser(userId, name, url)

      goodReadsUser.id must not beNull
    }
  }

  "GoodReads review list" should {
    "return list of reviews" in new WithApplication {
      val apiAuth = new ApiAuth()
      val reviewUri = "https://www.goodreads.com/review/list.xml?v=2&id=29274317"
      val response =
        Await.result(
          WS.url(reviewUri).sign(OAuthCalculator(apiAuth.KEY, requestToken)).get,
          5 seconds
        )

      response.status must be equalTo(200)
    }
  }
}