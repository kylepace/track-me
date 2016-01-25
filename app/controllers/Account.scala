package controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import models.Accounts
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

case class NewAccount(email: String, password: String, confirmPassword: String)

class Account @Inject() (accounts: Accounts) extends Controller with OptionalAuthElement with LoginLogout with AuthConfigImpl  {
  val createAccountForm = Form(
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

  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText
    )
  )

  def create = Action { implicit request =>
    Ok(views.html.register(createAccountForm))
  }

  def createPost = Action.async { implicit request =>
    createAccountForm.bindFromRequest.fold(
      badForm => Future.successful(BadRequest(views.html.register(badForm))),
      newAccount => {
        accounts.create(newAccount.email, newAccount.password)
        gotoLoginSucceeded(LoggedInAccount(newAccount.email, Administrator))
      }
    )
  }

  def login = Action { implicit req =>
    Ok(views.html.login(loginForm))
  }

  def loginPost = Action { implicit req =>
    Ok(views.html.login(loginForm))
  }
}
