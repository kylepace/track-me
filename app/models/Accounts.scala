package models

import anorm._
import anorm.SqlParser._
import com.github.t3hnar.bcrypt._
import play.api.db.DB
import play.api.Play.current

case class Account(id: Option[Long] = None, email: String, password: String){
  def encryptPassword = password.bcrypt
  def passwordMatches(pwd: String) = {
    println(pwd + "->" + pwd.bcrypt)
    pwd.isBcrypted(password)
  }
}

class Accounts {
  val accountParser = {
    long("id") ~
    str("email") ~
    str("password") map {
      case i~e~p => Account(Some(i), e, p)
    }
  }

  def create(email: String, password: String): Option[Account] = {
    val newAccount = Account(None, email, password)
    val encryptedPassword = newAccount.encryptPassword

    DB withConnection { implicit c =>
      val id: Option[Long] =
        SQL"INSERT INTO Account(email, password) VALUES ($email, ${encryptedPassword})"
          .executeInsert()

      id.map(i => Account(Some(i), email, encryptedPassword))
    }
  }

  def find(email: String): Option[Account] = {
    DB.withConnection { implicit c =>
      val account = SQL"SELECT TOP 1 * FROM Account WHERE email = $email"
        .as(accountParser *)

      if (account.size > 0) Some(account(0))
      else None
    }
  }
}