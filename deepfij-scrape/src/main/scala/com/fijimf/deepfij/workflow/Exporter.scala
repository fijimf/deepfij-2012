package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.{KeyedObject, ScheduleDao, Schedule}
import io.Source
import java.io.{FileInputStream, FileOutputStream, PrintWriter}
import org.apache.commons.lang.StringUtils

trait Exporter[T <: KeyedObject] {

  lazy val data: List[Map[String, String]] = {
    val inputStream = new FileInputStream(fileName)
    Source.fromInputStream(inputStream).getLines().map(s => {
      fromString(s)
    }).toList
  }

  def fileName: String

  def fromString(s: String): Map[String, String]

  def toString(t: T): String

  def export(key: String, f: Schedule => List[T]) {
    val sss: Option[Schedule] = new ScheduleDao().findByKey(key)
    sss.map(s => {
      println(sss)
      val w: PrintWriter = new PrintWriter(new FileOutputStream(fileName))
      f(s).sortBy(_.key).foreach(t => {
        w.println(toString(t))
      })
      w.close()
    })
  }

  def addNotBlank(map: Map[String, String], key: String, value: String): Map[String, String] = if (StringUtils.isNotBlank(value)) map + (key -> value) else map

}


