package models

import anorm._
import play.api.db.DB
import play.api.Play.current

sealed trait ApiType
case object GoodReads extends ApiType

case class ApiAccessor(id: Option[Long] = None, account: Account, name: ApiType, key: String)

class ApiAccess {
  def create(account: Account, name: ApiType, key: String): Option[ApiAccessor] = {
    DB withConnection { implicit c =>
      val id: Option[Long] =
        SQL("INSERT INTO ApiAccessor(account_id, name, key) VALUES ({account_id}, {name}, {key})")
          .on('account_id -> account.id, 'name -> name.toString(), 'key -> key)
          .executeInsert()

      id.map(i => ApiAccessor(Some(i), account, name, key))
    }
  }
}
