import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class AccountControllerSpec extends Specification {

  "Login" should {
    "render the login page" in new WithApplication {
      val login = route(FakeRequest(GET, "/account/login")).get

      status(login) must equalTo(OK)
      contentType(login) must beSome.which(_ == "text/html")
      contentAsString(login) must contain ("email")
      contentAsString(login) must contain ("password")
    }

    "return error when no account found" in new WithApplication {
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("doesnotexist@email.com"),
          "password" -> Seq("test")
        )
      )
      val req = FakeRequest(POST, "/account/login")
        .withBody(reqBody)
        .withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        .withHeaders(("X-Requested-With", CSRF.SignedTokenProvider.generateToken))

      val requestWithError = route(req).get
      status(requestWithError) must equalTo(BAD_REQUEST)
    }

    "return error when password mismatches" in new WithApplication {
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("testpasswordmismatch@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123456")
        )
      )
      val req = FakeRequest(POST, "/account/create")
        .withBody(reqBody)
        .withHeaders(("Csrf-Token", "nocheck"))

      val createdAccount = route(req).get
      status(createdAccount) must equalTo(SEE_OTHER)

      val loginBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("testpasswordmismatch@email.com"),
          "password" -> Seq("test")
        )
      )
      val loginReq = FakeRequest(POST, "/account/login")
        .withBody(loginBody)
        .withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        .withHeaders(("X-Requested-With", CSRF.SignedTokenProvider.generateToken))

      val requestWithError = route(loginReq).get
      status(requestWithError) must equalTo(BAD_REQUEST)
    }

    "redirects when login successful" in new WithApplication {
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("testpasswordmatches@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123456")
        )
      )
      val req = FakeRequest(POST, "/account/create")
        .withBody(reqBody)
        .withHeaders(("Csrf-Token", "nocheck"))

      val createdAccount = route(req).get
      status(createdAccount) must equalTo(SEE_OTHER)

      val loginBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("testpasswordmatches@email.com"),
          "password" -> Seq("123456")
        )
      )
      val loginReq = FakeRequest(POST, "/account/login")
        .withBody(loginBody)
        .withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        .withHeaders(("X-Requested-With", CSRF.SignedTokenProvider.generateToken))

      val redirectReq = route(loginReq).get
      status(redirectReq) must equalTo(SEE_OTHER)
    }
  }

  "Create Account" should {
    "render the create page" in new WithApplication {
      val home = route(FakeRequest(GET, "/account/create")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("email")
      contentAsString(home) must contain ("password")
      contentAsString(home) must contain ("confirmPassword")
    }

    "return error when passwords don't match" in new WithApplication {
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("test@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123")
        )
      )
      val req = FakeRequest(POST, "/account/create")
        .withBody(reqBody)
        .withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        .withHeaders(("X-Requested-With", CSRF.SignedTokenProvider.generateToken))

      val createAccount = route(req).get
      status(createAccount) must equalTo(BAD_REQUEST)
    }

    "render error when account already created" in new WithApplication {
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("testtaken@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123456")
        )
      )
      val req = FakeRequest(POST, "/account/create")
        .withBody(reqBody)
        .withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        .withHeaders(("X-Requested-With", CSRF.SignedTokenProvider.generateToken))

      val firstAccount = route(req).get
      Await.result(firstAccount, 5 seconds)

      val requestWithError = route(req).get
      status(requestWithError) must equalTo(BAD_REQUEST)
    }

    "render index page on account create" in new WithApplication {
      val token = CSRF.SignedTokenProvider.generateToken
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("test@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123456")
        )
      )
      val req = FakeRequest(POST, "/account/create")
        .withBody(reqBody)
        .withHeaders(("Csrf-Token", "nocheck"))

      val createdAccount = route(req).get
      status(createdAccount) must equalTo(SEE_OTHER)
      redirectLocation(createdAccount) must equalTo(Some("/"))
    }
  }
}