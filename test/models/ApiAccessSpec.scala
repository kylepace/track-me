package models

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.test._

@RunWith(classOf[JUnitRunner])
class ApiAccessSpec extends Specification {

  "ApiAccessor" should {
    "return new ApiAccess on create" in new WithApplication {
      val account = Account(Some(1), "email", "password")
      val apiAccessor = new ApiAccess().create(account, GoodReads, "authokey", "authsecret")
      apiAccessor.get.name should be (GoodReads)
    }

    "return new ApiAccess id on create" in new WithApplication {
      val account = Account(Some(1), "email", "password")
      val apiAccessor = new ApiAccess().create(account, GoodReads, "authkey", "authsecret")
      apiAccessor.get.id should beSome
    }
  }
}

