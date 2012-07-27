package com.fijimf.deepfij.workflow

import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat


trait ScraperConfig {
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def parseArgs(args: List[String]): ScraperConfig
}

case object Info extends ScraperConfig {

  override def parseArgs(args: List[String]) = this

}

case class FullRebuild(schedKey: String, schedName: String, fromDate: Date = new Date, toDate: Date) extends ScraperConfig {
  override def parseArgs(args: List[String]) = {
    args.toList match {
      case Nil => this
      case "-from" :: d :: tl => copy(fromDate = yyyymmdd.parse(d)).parseArgs(tl)
      case "-to" :: d :: tl => copy(toDate = yyyymmdd.parse(d)).parseArgs(tl)
      case "-key" :: key :: tl => copy(schedKey = key).parseArgs(tl)
      case "-name" :: name :: tl => copy(schedName = name).parseArgs(tl)
    }
  }
}

case class UpdateGamesAndResults(schedKey: String, fromDate: Date, toDate: Date) extends ScraperConfig {
  override def parseArgs(args: List[String]) = {
    args.toList match {
      case Nil => this
      case "-from" :: d :: tl => copy(fromDate = yyyymmdd.parse(d)).parseArgs(tl)
      case "-to" :: d :: tl => copy(toDate = yyyymmdd.parse(d)).parseArgs(tl)
      case "-key" :: key :: tl => copy(schedKey = key).parseArgs(tl)
    }
  }
}

case class UpdateResults(schedKey: String, fromDate: Date, toDate: Date) extends ScraperConfig {
  override def parseArgs(args: List[String]) = {
    args.toList match {
      case Nil => this
      case "-from" :: d :: tl => copy(fromDate = yyyymmdd.parse(d)).parseArgs(tl)
      case "-to" :: d :: tl => copy(toDate = yyyymmdd.parse(d)).parseArgs(tl)
      case "-key" :: key :: tl => copy(schedKey = key).parseArgs(tl)
    }
  }
}

object ConfigParser {
  def apply(args: Array[String]): ScraperConfig = {
    val l = args.toList
    l.headOption.map(_.toLowerCase) match {
      case Some("rebuild") => FullRebuild("schedule", "Schedule", today, today).parseArgs(l.tail)
      case Some("games") => UpdateGamesAndResults("schedule", today, today).parseArgs(l.tail)
      case Some("results") => UpdateResults("schedule", today, today).parseArgs(l.tail)
      case _ => Info
    }
  }


  lazy val today = DateUtils.truncate(new Date, Calendar.DATE)
}


