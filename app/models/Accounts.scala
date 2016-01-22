package models

import anorm._
import play.api.db.DB
import play.api.Play.current

case class Account(id: Option[Long] = None, email: String, password: String)

object Accounts {
  def create(email: String, password: String): Option[Account] = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL"INSERT INTO Account(email, password) VALUES ($email, $password)"
          .executeInsert()

      id.map(i => Account(Some(i), email, password))
    }
  }
}
