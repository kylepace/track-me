package models

import controllers.{GoodReadsApi, ApiAuth}
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.libs.oauth.{RequestToken, OAuthCalculator}
import play.api.libs.ws.WS
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class GoodReadsSpec extends Specification {

  val testTokenKey = "NVrj9NZFL6vxuHKjBLBa9Q"
  val testTokenSecret = "UbYbiOMj6fWYuFUaMFpeC8LyFQfcWYk8MVg8x6ItgoA"
  val requestToken = RequestToken(testTokenKey, testTokenSecret)

  "GoodReads current user" should {
    "return current user id" in new WithApplication {
      val api = new GoodReadsApi(new ApiAuth())

      val goodReadsUser = Await.result(api.getCurrentUser(requestToken), 5 seconds)

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