package com.fijimf.deepfij.data.kenpom


import com.fijimf.deepfij.util.TextScraper
import java.text.SimpleDateFormat

//"http://kenpom.com/cbbga12.txt"

case class KenPomScraper(url: String) extends TextScraper {
  val inFormat = new SimpleDateFormat("MM/dd/yyyy")
  val outFormat = new SimpleDateFormat("yyyyMMdd")
  lazy val gameData: List[(String, String, String, String, String)] = loadURL(url).filter(_.length>63).map(s => {
    val d = outFormat.format(inFormat.parse(s.substring(0, 10)))
    val at = s.substring(11, 33).trim()
    val as = s.substring(34, 37).trim()
    val ht = s.substring(38, 60).trim()
    val hs = s.substring(61, 64).trim()
    (d, ht, hs, at, as)
  })
}


//11/25/2011 Tennessee Martin        50 Mississippi St.         76