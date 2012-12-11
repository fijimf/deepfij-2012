package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.{ScheduleDao, BaseDao, Schedule, KeyedObject}
import java.util.Date
import org.joda.time.DateMidnight

trait Updater[T <: KeyedObject] extends Builder[T] with Verifier[T] {
  def loadAsOf(date: Date): List[Map[String, String]]

  def update(key: String, f: Schedule => List[T], asOf: Date = new DateMidnight().toDate): (List[T], List[T]) = {
    new ScheduleDao().findByKey(key).map(s => {
      val up: Map[String, T] = loadAsOf(asOf).flatMap(d => build(s, d)).map(x => x.key -> x).toMap
      val have: Map[String, T] = f(s).map(x => x.key -> x).toMap
      val updates = up.keySet.intersect(have.keySet).filter(k => !isSame(up(k), have(k)))
      val inserts = (up.keySet.diff(have.keySet) ++ updates).map(up(_))
      val deletes = (have.keySet.diff(up.keySet) ++ updates).map(have(_))
      (deletes.toList, inserts.toList)
    }).getOrElse(List.empty[T], List.empty[T])
  }

}