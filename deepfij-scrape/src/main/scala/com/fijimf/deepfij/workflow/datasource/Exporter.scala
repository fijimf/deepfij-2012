package com.fijimf.deepfij.workflow.datasource

import java.util.Date
import com.fijimf.deepfij.modelx.{ScheduleDao, Schedule}
import io.Source
import java.io.{InputStream, FileOutputStream, PrintWriter}
import org.apache.log4j.Logger
import antlr.StringUtils

abstract class Exporter[T] extends DataSource[T] {
  val log = Logger.getLogger(this.getClass)

  lazy val data: List[(String, String)] = {
    Source.fromInputStream(inputStream).getLines().map(s => {
      fromString(s)
    }).toList
  }


  def inputStream: InputStream

  def load: List[Map[String, String]] = {
    data
  }

  def loadAsOf(date: Date) = load

  def export(fileName: String, key: String, f: Schedule => List[T]) {
    new ScheduleDao().findByKey(key).map(s => {
      val w: PrintWriter = new PrintWriter(new FileOutputStream(fileFunc(key)))
      f(s).foreach(t => {
        w.println(toString(t))
      })
      w.close()
    })

  }

  def addNotBlank(map: Map[String, String], key: String, value: String): Map[String, String] = if (StringUtils.isNotBlank(value)) map + (key -> value) else map

}


