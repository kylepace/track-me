package models

import controllers.ApiAuth
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GoodReadsSpec extends Specification {

  "GoodReads OAuth" should {
    "return request token" in {
      val apiAuth = new ApiAuth()
      apiAuth.getGoodReadsRequestToken match {
        case Right(t) => t.token must not be null
        case Left(e) => throw e
      }
    }
  }
}


