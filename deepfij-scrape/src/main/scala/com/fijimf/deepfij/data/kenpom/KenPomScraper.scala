package com.fijimf.deepfij.data.kenpom

import java.text.SimpleDateFormat
import com.fijimf.deepfij.data.generic.{GameReader, HttpScraper}
import java.util.Date
import org.apache.commons.lang.time.DateUtils
import java.io.InputStream
import io.{BufferedSource, Source}

//"http://kenpom.com/cbbga12.txt"
case class KenPomScraper(url: String, aliasResource: String) extends HttpScraper with GameReader {
  val dfmt = new SimpleDateFormat("MM/dd/yyyy")
  lazy val gameData = loadTextPage(url).map(s => {
    val d = dfmt.parse(s.substring(0, 10))
    val at = s.substring(11, 33).trim()
    val as = s.substring(34, 37).trim().toInt
    val ht = s.substring(38, 60).trim
    val hs = s.substring(61, 64).trim().toInt
    (d, ht, Some(hs), at, Some(as))
  })

  lazy val aliasList = {
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(aliasResource)
    val src: BufferedSource = Source.fromInputStream(is)
    src.getLines().map(_.split(",")).map(arr => (arr(0), arr(1))).toList
  }

  def gameList(date: Date): List[(String, Option[Int], String, Option[Int])] = gameData.filter(g => DateUtils.isSameDay(g._1, date)).map(g => (g._2, g._3, g._4, g._5))

  def main(args: Array[String]) {
    gameData.foreach(println(_))

  }
}


//11/25/2011 Tennessee Martin        50 Mississippi St.         76