package models

import controllers.{GoodReadsApi, ApiAuth}
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.libs.oauth.{RequestToken}
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class GoodReadsSpec extends Specification {

  val testTokenKey = "8JREwkwPJkEVT4ITYwpNRA"
  val testTokenSecret = "GdLeeyowGPcGXLfIQarxjD1hWawXclz50Gus126qNvc"
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
      val goodReadsApi = new GoodReadsApi(new ApiAuth())

      val books = Await.result(goodReadsApi.getBooks("52427719", requestToken), 5 seconds)

      books.length must beGreaterThan(0)
      books.head.title must not beNull
    }
  }
}