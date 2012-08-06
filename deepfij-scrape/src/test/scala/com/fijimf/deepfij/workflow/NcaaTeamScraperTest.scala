package com.fijimf.deepfij.workflow

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import java.io.InputStreamReader
import com.fijimf.deepfij.util.HttpScraper

@RunWith(classOf[JUnitRunner])
class NcaaTeamScraperTest extends FunSuite {
  test("Strip names out of a teams by alpha page") {
    val stream = classOf[Deepfij].getClassLoader.getResourceAsStream("html/www.ncaa.com_schools_b.html")
    val teams: Seq[(String, String)] = NcaaTeamScraper.scrapeAlphaTeamsPage(new HttpScraper() {}.loadReader(new InputStreamReader(stream)))

    assertEquals(teams, List(
      ("babson", "Babson College"),
      ("benedictine-il", "Benedictine University (Illinois)"),
      ("bowdoin", "Bowdoin College"),
      ("baldwin-wallace", "Baldwin-Wallace College"),
      ("bentley", "Bentley University"),
      ("bowie-st", "Bowie State University"),
      ("ball-st", "Ball State University"),
      ("berry", "Berry College"),
      ("bowling-green", "Bowling Green State University"),
      ("baptist-bible-pa", "Baptist Bible College"),
      ("bethany-wv", "Bethany College (West Virginia)"),
      ("bradley", "Bradley University"),
      ("bard", "Bard College"),
      ("bethany-lutheran", "Bethany Lutheran College"),
      ("brandeis", "Brandeis University"),
      ("barry", "Barry University"),
      ("bethel-mn", "Bethel University (Minnesota)"),
      ("brevard", "Brevard College"),
      ("barton", "Barton College"),
      ("bethune-cookman", "Bethune-Cookman University"),
      ("bridgewater-va", "Bridgewater College (Virginia)"),
      ("baruch", "Baruch College"),
      ("binghamton", "Binghamton University"),
      ("bridgewater-st", "Bridgewater State University"),
      ("bates", "Bates College"),
      ("birmingham-so", "Birmingham-Southern College"),
      ("byu", "Brigham Young University"),
      ("bay-path", "Bay Path College"),
      ("black-hills-st", "Black Hills State University"),
      ("byu-hawaii", "Brigham Young University, Hawaii"),
      ("baylor", "Baylor University"), ("blackburn", "Blackburn College"),
      ("brooklyn", "Brooklyn College"),
      ("becker", "Becker College"),
      ("bloomfield", "Bloomfield College"),
      ("brown", "Brown University"),
      ("bellarmine", "Bellarmine University"),
      ("bloomsburg", "Bloomsburg University of Pennsylvania"),
      ("bryant", "Bryant University"),
      ("belmont-abbey", "Belmont Abbey College"),
      ("bluefield-st", "Bluefield State College"),
      ("bryn-mawr", "Bryn Mawr College"),
      ("belmont", "Belmont University"),
      ("bluffton", "Bluffton University"),
      ("bucknell", "Bucknell University"),
      ("beloit", "Beloit College"),
      ("boise-st", "Boise State University"),
      ("buena-vista", "Buena Vista University"),
      ("bemidji-st", "Bemidji State University"),
      ("boston-college", "Boston College"),
      ("buffalo-st", "Buffalo State College"),
      ("benedict", "Benedict College"),
      ("boston-u", "Boston University"),
      ("butler", "Butler University"))
    )
  }
}
