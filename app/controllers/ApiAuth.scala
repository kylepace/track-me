package controllers

import javax.inject.Inject

import play.api.libs.oauth._
import play.api.libs.ws.WS
import play.api.mvc.RequestHeader

import scala.collection.immutable.Seq
import scala.concurrent.{Future}
import scala.xml.{Node, XML}

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

case class GoodReadsUser(id: String, name: String, url: String)
case class GoodReadsBook(id: Int, title: String, url: String, imageUrl: String, author: String)

class GoodReadsApi @Inject() (apiAuth: ApiAuth) {
  import play.api.Play.current
  import scala.concurrent.ExecutionContext.Implicits.global

  def getCurrentUser(token: RequestToken): Future[GoodReadsUser] = {
    val uri = "https://www.goodreads.com/api/auth_user"
    val request = WS.url(uri).sign(OAuthCalculator(apiAuth.KEY, token))
    request.get.flatMap { r => Future.successful(parseGoodReadsUser(r.body)) }
  }

  def getBooks(userId: String, token: RequestToken): Future[Seq[GoodReadsBook]] = {
    val reviewUri = s"https://www.goodreads.com/review/list.xml?v=2&id=${userId}"
    val request = WS.url(reviewUri).sign(OAuthCalculator(apiAuth.KEY, token))
    request.get.flatMap{ r => Future.successful(parseGoodReadsBook(r.body)) }
  }

  def parseGoodReadsUser(xml: String): GoodReadsUser = {
    val xmlDoc = XML.loadString(xml)
    GoodReadsUser(
      (xmlDoc \ "user").\@("id"),
      (xmlDoc \ "name").text,
      (xmlDoc \ "link").text
    )
  }

  def parseGoodReadsBook(xml: String): Seq[GoodReadsBook] = {
    val xmlDoc = XML.loadString(xml)
    val reviewXml = (xmlDoc \ "reviews" \ "review")
    reviewXml map parseBookFromReviewXml
  }

  def parseBookFromReviewXml(xml: Node): GoodReadsBook = {
    val book = xml \ "book"
    val bookId = (book \ "id").text.toInt
    val title = (book \ "title").text
    val url = (book \ "link").text
    val imageUrl = (book \ "image_url").text
    val author = ((book \ "authors" \ "author").head \ "name").text
    GoodReadsBook(bookId, title, url, imageUrl, author)
  }
}