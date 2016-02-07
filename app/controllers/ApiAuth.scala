package controllers

import play.api.libs.oauth.{RequestToken, ServiceInfo, OAuth, ConsumerKey}
import play.api.mvc.RequestHeader

class ApiAuth {
  val key = "vq5wD2sQi3iVi4uWc2uffQ"
  val secret = "W2j84y2h9gYI3cvMWxOk1DMiQcpYunk34zZcfee34"
  val goodReadsAuthUrl = "http://localhost:9000/auth/goodreads"

  val KEY = ConsumerKey(key, secret)

  val GoodReadsOAuth = OAuth(ServiceInfo(
    "http://www.goodreads.com/oauth/request_token",
    "http://www.goodreads.com/oauth/access_token",
    "http://www.goodreads.com/oauth/authorize", KEY),
    true)

  def getGoodReadsRedirectUrl(token: String) = GoodReadsOAuth.redirectUrl(token)

  def getGoodReadsRequestToken = GoodReadsOAuth.retrieveRequestToken(goodReadsAuthUrl)

  def getGoodReadsAuthToken(tokenPair: RequestToken, verifier: String) =
    GoodReadsOAuth.retrieveAccessToken(tokenPair, verifier)

  def goodReadsTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get("goodreads_token")
      secret <- request.session.get("goodreads_secret")
    } yield {
      RequestToken(token, secret)
    }
  }
}
