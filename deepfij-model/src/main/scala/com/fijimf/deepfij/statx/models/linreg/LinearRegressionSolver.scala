package com.fijimf.deepfij.statx.models.linreg

trait LinearRegressionSolver {
  def solve(A: Map[(Int, Int), Double], aRows: Int, aCols: Int, b: List[Double]): List[Double]
}
