package com.fijimf.deepfij.statx.models.linreg

import org.apache.mahout.math.{DenseVector, RandomAccessSparseVector, SparseMatrix}
import org.apache.mahout.math.solver.LSMR

object LSMRSolver extends LinearRegressionSolver {

  def solve(A: Map[(Int, Int), Double], aRows: Int, aCols: Int, b: List[Double]): List[Double] = {
    val Ai: SparseMatrix = createRASparseMatrix(A, aRows, aCols)
    val bi = new DenseVector(b.toArray)
    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(100)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val xi = lsmr.solve(Ai, bi)
    0.until(aCols).map(i => xi.get(i)).toList

  }

  private[this] def createRASparseMatrix(A: Map[(Int, Int), Double], aRows: Int, aCols: Int): SparseMatrix = {
    val a: java.util.Map[java.lang.Integer, RandomAccessSparseVector] = new java.util.HashMap[java.lang.Integer, RandomAccessSparseVector]()
    A.foreach {
      case (p: (Int, Int), d: Double) => {
        if (a.containsKey(p._1)) {
          a.get(p._1).set(p._2, d)
        } else {
          val row = new RandomAccessSparseVector(aCols)
          row.set(p._2, d)
          a.put(p._1, row)
        }
      }
    }

    new SparseMatrix(aRows, aCols, a)

  }
}
