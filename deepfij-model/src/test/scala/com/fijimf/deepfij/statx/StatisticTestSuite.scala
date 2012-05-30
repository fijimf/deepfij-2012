/*
 * Created by IntelliJ IDEA.
 * User: fijimf
 * Date: 5/29/12
 * Time: 11:58 PM
 */
package com.fijimf.deepfij.statx

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.fijimf.deepfij.modelx.Schedule
import java.util.Date
import java.text.SimpleDateFormat
import scala.Predef._

@RunWith(classOf[JUnitRunner])
class StatisticTestSuite extends FunSuite {
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")
  val d1 = yyyymmdd.parse("20120501")
  val d2 = yyyymmdd.parse("20120502")
  val d3 = yyyymmdd.parse("20120503")
  val d4 = yyyymmdd.parse("20120504")
  val d6 = yyyymmdd.parse("20120506")
  val st: Statistic[String] = new Statistic[String]() {
    val data = Map(
      d1 -> Map("A" -> 1.0, "B" -> 2.0, "C" -> 2.0, "D" -> 3.0, "E" -> 12.0),
      d2 -> Map("A" -> 1.0, "B" -> 2.0, "C" -> 2.0, "D" -> 4.0, "E" -> 13.0),
      d3 -> Map("A" -> 1.0, "B" -> 2.0, "C" -> 2.0, "D" -> 5.0, "E" -> 14.0),
      d4 -> Map("A" -> 1.0, "B" -> 2.0, "C" -> 2.0, "D" -> 6.0, "E" -> 15.0),
      d6 -> Map("A" -> 1.0, "B" -> 2.0, "C" -> 2.0, "D" -> 7.0, "E" -> 16.0, "F" -> 6.0)
    )

    def keys(s: Schedule) = (data.values.map(_.keys).flatten).toSet.toList.sorted

    def startDate(s: Schedule) = d1

    def endDate(s: Schedule) = d6

    def function(s: Schedule, k: String, d: Date) = data.get(d).flatMap(_.get(k))

    def name = "Test"

    def higherIsBetter = true
  }
  test("Population basics") {
    val pop: Population[String] = st.population(null, d1)
    assert(pop.name == "Test")
    assert(pop.keys == List("A", "B", "C", "D", "E", "F"))
    assert(pop.date == d1)
    assert(pop.stat("A") == Some(1))
    assert(pop.stat("B") == Some(2))
    assert(pop.stat("C") == Some(2))
    assert(pop.stat("D") == Some(3))
    assert(pop.stat("E") == Some(12))
    assert(pop.stat("F") == None)
  }
  test("Population standard rank, one tie, missing data") {
    val pop: Population[String] = st.population(null, d1)
    assert(pop.rank("A") == Some(5))
    assert(pop.rank("B") == Some(3))
    assert(pop.rank("C") == Some(3))
    assert(pop.rank("D") == Some(2))
    assert(pop.rank("E") == Some(1))
    assert(pop.rank("F") == None)
  }

  test("Population fractional rank, one tie, missing data") {
    val pop: Population[String] = st.population(null, d1)
    assert(pop.fractionalRank("A") == Some(5))
    assert(pop.fractionalRank("B") == Some(3.5))
    assert(pop.fractionalRank("C") == Some(3.5))
    assert(pop.fractionalRank("D") == Some(2))
    assert(pop.fractionalRank("E") == Some(1))
    assert(pop.fractionalRank("F") == None)
  }

  test("Population max, min, med") {
    val pop: Population[String] = st.population(null, d1)
    assert(pop.count == 5)
    assert(pop.missing == 1)
    assert(pop.max == Some(12.0))
    assert(pop.min == Some(1.0))
    assert(pop.med == Some(2.0))
    //     assert(pop.maxItem == List("E"))
    //     assert(pop.minItem == List("A"))
  }

  test("Population mean, std dev ") {
    val pop: Population[String] = st.population(null, d1)

    assertEquals(pop.mean, 4.0, 0.01)

    assertEquals(pop.stdDev, 4.049691346263317, 0.01)

  }


}