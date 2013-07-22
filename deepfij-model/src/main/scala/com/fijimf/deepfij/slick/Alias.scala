package com.fijimf.deepfij.slick

package com.fijimf.deepfij.slick

case class Alias(id: Long,
                 teamId: Long,
                 alias: String)

trait AliasDao {

  self: Profile =>

  import profile.simple._

  object Aliases extends Table[Alias]("alias") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def teamId = column[Long]("team_id")

    def alias = column[String]("alias")

    def * = id ~ teamId ~ alias <>(Alias.apply _, Alias.unapply _)

    def autoInc = id ~ teamId ~ alias <>(Alias.apply _, Alias.unapply _)


  }

}
