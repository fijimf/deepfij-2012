package com.fijimf.deepfij.statx

import java.util.Date

case class ModelValues[T](values: Map[Date, Map[T, Double]] = Map.empty[Date, Map[T, Double]]) {

  def get(d: Date, t: T): Option[Double] = for (v <- values.get(d); x <- v.get(t)) yield x

  def update(d: Date, t: T, x: Double): ModelValues[T] = {
    val revisedValue = values.get(d) match {
      case Some(m) => m + (t -> x)
      case None => Map(t -> x)
    }
    ModelValues(values + (d -> revisedValue))
  }
}