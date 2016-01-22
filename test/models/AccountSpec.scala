package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class AccountSpec extends Specification {

  "Accounts" should {
    "return new account on create" in new WithApplication {
      val account = Accounts.create("test@test.com", "oijasd98fhejdohj2uhsdfi")



    }
  }
}
