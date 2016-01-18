package controllers

import javax.inject.Inject

import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

case class NewAccount(email: String, password: String, confirmPassword: String)

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

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

  def createAccount = Action { implicit request =>
    accountForm.bindFromRequest.fold(
      badForm => {
        BadRequest(views.html.register(badForm))
      },
      success => {
        Redirect(routes.Application.index())
      }
    )
  }
}
