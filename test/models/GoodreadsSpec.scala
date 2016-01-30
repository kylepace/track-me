package models

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.libs.oauth.{ConsumerKey, OAuth, ServiceInfo}

@RunWith(classOf[JUnitRunner])
class GoodReadsSpec extends Specification {
  val key = "vq5wD2sQi3iVi4uWc2uffQ"
  val secret = "W2j84y2h9gYI3cvMWxOk1DMiQcpYunk34zZcfee34"

  val KEY = ConsumerKey(key, secret)

  val GoodReadsOAuth = OAuth(ServiceInfo(
    "http://www.goodreads.com/oauth/request_token",
    "http://www.goodreads.com/oauth/access_token",
    "http://www.goodreads.com/oauth/authorize", KEY),
    true)

  "Goodreads OAuth" should {
    "return request token" in {
      GoodReadsOAuth.retrieveRequestToken("http://localhost:9000/goodreadsaccept") match {
        case Right(t) => t.token must not be null
        case Left(e) => throw e
      }
    }

    
  }
}


