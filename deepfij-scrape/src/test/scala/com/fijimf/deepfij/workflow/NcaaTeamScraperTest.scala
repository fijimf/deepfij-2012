package com.fijimf.deepfij.workflow

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import xml.XML

@RunWith(classOf[JUnitRunner])
class NcaaTeamScraperTest extends FunSuite {
  test("Strip names out of a teams by alpha page") {
    val stream = classOf[Deepfij].getClassLoader.getResourceAsStream("html/www.ncaa.com_schools_b.html")
    val teams: Seq[(String, String)] = NcaaTeamScraper.scrapeAlphaTeamsPage(XML.load(stream))
    println(teams)
  }
}
