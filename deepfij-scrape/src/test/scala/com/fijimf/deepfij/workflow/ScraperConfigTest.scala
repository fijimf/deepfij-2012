package com.fijimf.deepfij.workflow

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.text.SimpleDateFormat

@RunWith(classOf[JUnitRunner])
class ScraperConfigTest extends FunSuite {
  val format = new SimpleDateFormat("yyyyMMdd")

  test("No args should return Info") {
    val c = ConfigParser(Array.empty[String])
    assert(c === Info)
  }
  test("Bad mode should return Info") {
    val c = ConfigParser(Array("I suck"))
    assert(c === Info)
  }
  test("'rebuild' should return FullRebuild") {
    assert(ConfigParser(Array("rebuild")) === FullRebuild("schedule", "Schedule", ConfigParser.today, ConfigParser.today))
    assert(ConfigParser(Array("Rebuild")) === FullRebuild("schedule", "Schedule", ConfigParser.today, ConfigParser.today))
    assert(ConfigParser(Array("REBUILD")) === FullRebuild("schedule", "Schedule", ConfigParser.today, ConfigParser.today))

  }

  test("'rebuild' with args ") {
    assert(ConfigParser(Array("rebuild", "-key", "2012", "-name", "NCAA 2012")) === FullRebuild("2012", "NCAA 2012", ConfigParser.today, ConfigParser.today))
    assert(ConfigParser(Array("rebuild", "-name", "NCAA 2012", "-key", "2012")) === FullRebuild("2012", "NCAA 2012", ConfigParser.today, ConfigParser.today))
    assert(ConfigParser(Array("Rebuild", "-name", "NCAA 2012", "-key", "2012", "-from", "20111106")) === FullRebuild("2012", "NCAA 2012", format.parse("20111106"), ConfigParser.today))
    assert(ConfigParser(Array("Rebuild", "-name", "NCAA 2012", "-key", "2012", "-from", "20111106")) === FullRebuild("2012", "NCAA 2012", format.parse("20111106"), ConfigParser.today))
    assert(ConfigParser(Array("Rebuild", "-from", "20111106", "-name", "NCAA 2012", "-key", "2012")) === FullRebuild("2012", "NCAA 2012", format.parse("20111106"), ConfigParser.today))

  }

}