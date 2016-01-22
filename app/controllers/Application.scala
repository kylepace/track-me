package controllers

import jp.t2v.lab.play2.auth.{OptionalAuthElement, LoginLogout}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

case class NewAccount(email: String, password: String, confirmPassword: String)

class Application extends Controller with OptionalAuthElement with LoginLogout with AuthConfigImpl {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val accountForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(NewAccount.apply)(NewAccount.unapply)
    verifying("Passwords must match.", a => a.password == a.confirmPassword)
  )

  def register = Action { implicit request =>
    Ok(views.html.register(accountForm))
  }

  def createAccount = AsyncStack { implicit request =>
    implicit val user = loggedIn.getOrElse(null)
    accountForm.bindFromRequest.fold(
      badForm => Future.successful(BadRequest(views.html.register(badForm))),
      newAccount => {
        gotoLoginSucceeded(LoggedInAccount(newAccount.email, Administrator))
      }
    )
  }
}
