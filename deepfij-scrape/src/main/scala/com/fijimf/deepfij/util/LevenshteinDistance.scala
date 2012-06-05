package com.fijimf.deepfij.util


object LevenshteinDistance {
  def apply(s: String, t: String): Int = {
    require(s != null && t != null)
    (s.length(), t.length()) match {
      case (n, 0) => n
      case (0, m) => m
      case (n, m) => {
        val d = Array.tabulate(n + 1, m + 1)((i, j) => {
          (i, j) match {
            case (k, 0) => k
            case (0, k) => k
            case (k, l) => 0
          }
        })
        for (val i <- 1 to n; val j <- 1 to m) {
          val cost = if (s(i - 1) == t(j - 1)) 0 else 1

          d(i)(j) = List(
            d(i - 1)(j) + 1, // deletion
            d(i)(j - 1) + 1, // insertion
            d(i - 1)(j - 1) + cost // substitution
          ).min
        }
        d(n)(m)
      }
    }
  }

  def main(args: Array[String]) {
    println(LevenshteinDistance("JIM", "JOHN"))
  }
}