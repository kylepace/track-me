import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class HomeControllerSpec extends Specification {

  "Home" should {
    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
    }
  }
}