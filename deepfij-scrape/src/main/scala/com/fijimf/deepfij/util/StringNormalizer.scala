package com.fijimf.deepfij.util

import java.lang.String
import collection.immutable.{Map, List}


object StringNormalizer {
  def normalize(observed: List[String], target: List[String]): Map[String, String] = {
    if (observed.isEmpty) {
      Map.empty[String, String]
    } else {
      println("---------")
      println("Observed: "+observed.size)
      println("Target: "+ target.size)
      val bestScoreList = observed.map(o => {
        val distances = target.map(x => {
          x -> LevenshteinDistance(o, x)
        })
        (o -> distances.minBy(_._2))
      })
      val bestScore = bestScoreList.map(_._2._2).min
      println("Best Score "+bestScore)
      val (matched, retry) = bestScoreList.partition(_._2._2 == bestScore)
      val map = matched.map(t => (t._1 -> t._2._1)).toMap
      val usedTargets: List[String] = map.values.toList
      val unused = target.filterNot(usedTargets.contains(_))
      println("Results: "+map.size)
      map ++ normalize(retry.map(_._1), unused)
    }
  }

  def apply(observed: List[String], target: List[String]): Map[String, String] = {
    val obs = observed.map(n => (n -> n.toLowerCase.replaceAll(" ", "-").replaceAll("[^a-z-]", ""))).toMap
    val n = normalize(obs.values.toList, target)
    n.foreach(println(_))
    obs.map{case (k: String, v: String) => {
      (k->n(obs(k)))
    }}.toMap
  }


  def main(args: Array[String]) {
    val m: Map[String, String] = apply(List("Jim", "John", "Tommy"), List("Jimmy", "Jon", "Thomas"))
    println(m)
  }
}