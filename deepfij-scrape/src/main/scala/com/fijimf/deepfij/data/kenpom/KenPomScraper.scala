package com.fijimf.deepfij.data.kenpom

import java.text.SimpleDateFormat

import com.fijimf.deepfij.util.{TextScraper, HttpScraper}
import com.fijimf.deepfij.data.generic.GameReader
import java.util.Date
import org.apache.commons.lang.time.DateUtils
import java.io.InputStream
import io.{BufferedSource, Source}

//"http://kenpom.com/cbbga12.txt"

case class KenPomScraper(url: String) extends TextScraper {

  lazy val gameData: List[(String, String, String, String, String)] = loadURL(url).map(s => {
    val d = s.substring(0, 10)
    val at = s.substring(11, 33).trim()
    val as = s.substring(34, 37).trim()
    val ht = s.substring(38, 60).trim()
    val hs = s.substring(61, 64).trim()
    (d, ht, hs, at, as)
  })
}


//11/25/2011 Tennessee Martin        50 Mississippi St.         76