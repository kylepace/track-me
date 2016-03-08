package controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import models._
import play.api.mvc.Controller

class GoodReads @Inject() (apiAuth: ApiAuth, apiAccess: ApiAccess, accounts: Accounts) extends Controller with AuthElement with AuthConfigImpl {
  def loggedInAccount(implicit request: RequestWithAttributes[_]) = accounts.find(loggedIn.email).get

  def auth = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    request.getQueryString("oauth_token").map { verifier =>
      val tokenPair = apiAuth.goodReadsTokenPair(request).get
      apiAuth.getGoodReadsAuthToken(tokenPair, verifier) match {
        case Right(t) => {
          val apiAccessor = apiAccess.create(loggedInAccount, GoodReads, t.token, t.secret)
          Ok(s"got your goodreads ${apiAccessor.get.id}")
            .withSession("goodreads_token" -> t.token, "goodreads_secret" -> t.secret)
        }
        case Left(e) => throw e
      }
    }.getOrElse(
      apiAuth.getGoodReadsRequestToken match {
        case Right(t) => {
          // make sure this gets encrypted
          Redirect(apiAuth.getGoodReadsRedirectUrl(t.token))
              .withSession("goodreads_token" -> t.token, "goodreads_secret" -> t.secret)
        }
        case Left(e) => throw e
      }
    )
  }
}
