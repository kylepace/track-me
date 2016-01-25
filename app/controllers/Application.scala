package controllers

import javax.inject.Inject
import jp.t2v.lab.play2.auth.{OptionalAuthElement, LoginLogout}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import models._

case class NewAccount(email: String, password: String, confirmPassword: String)

class Application @Inject() (accounts: Accounts) extends Controller with OptionalAuthElement with LoginLogout with AuthConfigImpl {

  val accountForm = Form(
    mapping(
      "email" -> email.verifying("Email already taken.", e => accounts.find(e) match {
        case Some(_) => false
        case None => true
      }),
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(NewAccount.apply)(NewAccount.unapply)
      verifying("Passwords must match.", a => a.password == a.confirmPassword)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def register = Action { implicit request =>
    Ok(views.html.register(accountForm))
  }

  def createAccount = Action.async { implicit request =>
    accountForm.bindFromRequest.fold(
      badForm => Future.successful(BadRequest(views.html.register(badForm))),
      newAccount => {
        accounts.create(newAccount.email, newAccount.password)
        gotoLoginSucceeded(LoggedInAccount(newAccount.email, Administrator))
      }
    )
  }
}
