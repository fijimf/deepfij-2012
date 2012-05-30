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

@RunWith(classOf[JUnitRunner])
class StatisticTestSuite extends FunSuite {

  test("Population basics") {
    val st: Statistic[String] = new Statistic[String]() {
      val data = Map("A" -> 1.0, "B" -> 2.0, "C" -> 2.0, "D" -> 3.0, "E" -> 12.0)

      def keys(s: Schedule) = data.keys.toList.sorted

      def startDate(s: Schedule) = null

      def endDate(s: Schedule) = null

      def function(s: Schedule, k: String, d: Date) = data.get(k)

      def name = "Test"

      def higherIsBetter = true
    }
    val pop: Population[String] = st.population(null, null)


    assert(pop.keys == List("A", "B", "C", "D", "E"))
    assert(pop.stat("A") == Some(1))
    assert(pop.stat("B") == Some(2))
    assert(pop.stat("C") == Some(2))
    assert(pop.stat("D") == Some(3))
    assert(pop.stat("E") == Some(12))
    assert(pop.stat("F") == None)

    assert(pop.rank("A") == Some(5))
    assert(pop.rank("B") == Some(3))
    assert(pop.rank("C") == Some(3))
    assert(pop.rank("D") == Some(2))
    assert(pop.rank("E") == Some(1))
    assert(pop.rank("F") == None)

    assert(pop.fractionalRank("A") == Some(5))
    assert(pop.fractionalRank("B") == Some(3.5))
    assert(pop.fractionalRank("C") == Some(3.5))
    assert(pop.fractionalRank("D") == Some(2))
    assert(pop.fractionalRank("E") == Some(1))
    assert(pop.fractionalRank("F") == None)


  }


}