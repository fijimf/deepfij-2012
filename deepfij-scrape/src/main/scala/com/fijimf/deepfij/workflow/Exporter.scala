package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.{KeyedObject, ScheduleDao, Schedule}
import io.Source
import java.io.{InputStream, FileOutputStream, PrintWriter}
import org.apache.commons.lang.StringUtils

trait Exporter[T <: KeyedObject] {

  lazy val data: List[Map[String, String]] = {
    Source.fromInputStream(inputStream).getLines().map(s => {
      fromString(s)
    }).toList
  }

  def fromString(s: String): Map[String, String]

  def toString(t: T): String

  def inputStream: InputStream

  def export(fileName: String, key: String, f: Schedule => List[T]) {
    new ScheduleDao().findByKey(key).map(s => {
      val w: PrintWriter = new PrintWriter(new FileOutputStream(fileName))
      f(s).sortBy(_.key).foreach(t => {
        w.println(toString(t))
      })
      w.close()
    })
  }

  def addNotBlank(map: Map[String, String], key: String, value: String): Map[String, String] = if (StringUtils.isNotBlank(value)) map + (key -> value) else map

}


