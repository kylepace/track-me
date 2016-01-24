package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._

@RunWith(classOf[JUnitRunner])
class AccountSpec extends Specification {

  "Accounts" should {
    "return new account on create" in new WithApplication {
      val account = Accounts.create("test@test.com", "oijasd98fhejdohj2uhsdfi").get
      assert(account.email == "test@test.com", "Emails do not match.")
    }

    "encrypt password begore saving" in new WithApplication {
      val password = "asidfjouhwijb"
      val account = Accounts.create("test1@test.com", password).get
      assert(account.password != password, "Password was not encrypted")
    }

    "find an account by email" in new WithApplication {
      val email = "test2@test.com"
      val newAccount = Accounts.create(email, "skdjfnaisduhf").get
      val foundAccount = Accounts.find(email).get
      assert(foundAccount.email == email)
    }
  }
}
