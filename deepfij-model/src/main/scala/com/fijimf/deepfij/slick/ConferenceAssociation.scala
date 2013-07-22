package com.fijimf.deepfij.slick

case class ConferenceAssociation(id: Long,
                                 seasonId: Long,
                                 conferenceId: Long,
                                 teamId: Long)

trait ConferencesAssociationDao {

  self: Profile =>

  import profile.simple._

  object ConferenceAssociations extends Table[ConferenceAssociation]("conference_associations") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def seasonId = column[Long]("season_id")

    def conferenceId = column[Long]("conference_id")

    def teamId = column[Long]("team_id")

    def * = id ~ seasonId ~ conferenceId ~ teamId <>(ConferenceAssociation.apply _, ConferenceAssociation.unapply _)

    def autoInc = seasonId ~ conferenceId ~ teamId returning id

  }


}