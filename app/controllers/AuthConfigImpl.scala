package controllers

import jp.t2v.lab.play2.auth._
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

case class LoggedInAccount(email: String, role: Role)

sealed trait Role
case object Administrator extends Role
case object NormalUser extends Role

trait AuthConfigImpl extends AuthConfig {
  type Id = LoggedInAccount

  type User = LoggedInAccount

  type Authority = Role

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds: Int = 3600

  def resolveUser(id: Id)(implicit ctx: ExecutionContext) = Future.successful(Some(id))

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.Home.index))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.Home.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.Home.index))

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.role, authority) match {
      case (Administrator, _)       => true
      case (NormalUser, NormalUser) => true
      case _                        => false
    }
  }

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext) = {
    Future.successful(Forbidden("no permission"))
  }
}
