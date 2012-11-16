package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.{KeyedObject, ScheduleDao, Schedule}
import io.Source
import java.io.{FileInputStream, FileOutputStream, PrintWriter}
import org.apache.commons.lang.StringUtils
import com.fijimf.deepfij.util.Logging

trait Exporter[T <: KeyedObject] {
  self: Logging =>

  lazy val data: List[Map[String, String]] = {
    val inputStream = new FileInputStream(dataDir + "/" + fileName)
    Source.fromInputStream(inputStream).getLines().map(s => {
      fromString(s)
    }).toList
  }

  def dataDir: String

  def fileName: String

  def fromString(s: String): Map[String, String]

  def toString(t: T): String

  def export(key: String, f: Schedule => List[T]) {
    log.info("Snapshotting %s into %s ".format(key, dataDir + "/" + fileName))
    new ScheduleDao().findByKey(key).map(s => {
      val w: PrintWriter = new PrintWriter(new FileOutputStream(dataDir + "/" + fileName))
      f(s).sortBy(_.key).foreach(t => {
        w.println(toString(t))
      })
      w.close()
    })
  }

  def addNotBlank(map: Map[String, String], key: String, value: String): Map[String, String] = if (StringUtils.isNotBlank(value)) map + (key -> value) else map

}


