import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._
import play.filters.csrf.CSRF

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
    }

    "render the register page" in new WithApplication{
      val home = route(FakeRequest(GET, "/register")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("email")
      contentAsString(home) must contain ("password")
      contentAsString(home) must contain ("confirmPassword")
    }

    "return error when passwords don't match" in new WithApplication{
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("test@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123")
        )
      )
      val req = FakeRequest(POST, "/register")
        .withBody(reqBody)
        .withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        .withHeaders(("X-Requested-With", CSRF.SignedTokenProvider.generateToken))

      val createAccount = route(req).get
      status(createAccount) must equalTo(BAD_REQUEST)
    }

    "render index page on account create" in new WithApplication{
      val token = CSRF.SignedTokenProvider.generateToken
      val reqBody = AnyContentAsFormUrlEncoded(
        Map(
          "email" -> Seq("test@email.com"),
          "password" -> Seq("123456"),
          "confirmPassword" -> Seq("123456")
        )
      )
      val req = FakeRequest(POST, "/register")
        .withBody(reqBody)
        .withHeaders(("Csrf-Token", "nocheck"))

      val createAccount = route(req).get
      status(createAccount) must equalTo(SEE_OTHER)
      redirectLocation(createAccount) must equalTo(Some("/"))
    }
  }
}
