package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.data.generic.{GameReader, ConferenceReader, TeamReader}
import com.fijimf.deepfij.data.ncaa.{NcaaTeamScraper, NcaaGameScraper}
import com.fijimf.deepfij.repo.{TeamData, ScheduleRepository}
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.util.Date
import com.fijimf.deepfij.util.{DateStream, StringNormalizer}
import collection.immutable.{List, Map}
import org.apache.log4j.Logger

case class Scraper(t: TeamReader, c: ConferenceReader, g: GameReader) {
  val logger = Logger.getLogger(this.getClass)
  val repo = new ScheduleRepository

  def scrape(conf: ScraperConfig) = {
    conf match {
      case Info =>
      case r: FullRebuild => {
        val repo = new ScheduleRepository
        repo.dropCreateSchedule(r.schedKey, r.schedName)
        repo.createConferences(r.schedKey, c.conferenceMap)
        repo.createTeams(r.schedKey, t.teamData)
        repo.createTeamAliases(r.schedKey, g.aliasList.toMap)
        logger.info("Loading games between " + r.fromDate + "," + r.toDate)
        DateStream(r.fromDate, r.toDate).foreach((date: Date) => {
          logger.info("Loading games for "+date)
          val gs: List[(String, Option[Int], String, Option[Int])] = g.gameList(date)
          repo.createGames(r.schedKey, gs.map {
            case (h, hs, a, as) => (date, h, a)
          })
          repo.updateResults(r.schedKey, gs.filter {
            case (h: String, hs: Option[Int], a: String, as: Option[Int]) => hs.isDefined && as.isDefined
          }.map {
            case (h: String, hs: Option[Int], a: String, as: Option[Int]) => (date, h, hs.get, a, as.get)
          })
        })
      }
      case u: UpdateGamesAndResults => {
        val s = repo.scheduleDao.findByKey(u.schedKey).get
        DateStream(u.fromDate, u.toDate).foreach((date: Date) => {
          val ggs = g.gameList(date)
          repo.createGames(u.schedKey, ggs.map {
            case (h, hs, a, as) => (date, h, a)
          })
        })
      }
    }
  }
}


object Main {

  def main(args: Array[String]) {
    new Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt")).scrape(ConfigParser(args))
  }

}