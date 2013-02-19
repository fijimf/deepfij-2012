package com.fijimf.deepfij.statx.models

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.apache.mahout.math.SparseMatrix

@RunWith(classOf[JUnitRunner])
class LinearRegressionSolverTestSuite extends FunSuite {
  test("Simplest") {
    val A = Map((0,0)-> 1.0)
    val b = List(5.0)
    val x: List[Double] = LSMRSolver.solve(A, 1, 1, b)
    assert(x.size==1)
    assert(x(0)==5.0)
  }

  test("Simplest2") {
    val A = Map(
      (0,0)-> 1.0,
      (1,0)-> 3.0
    )
    val b = List(5.0, 15.0)
    val x: List[Double] = LSMRSolver.solve(A,2, 1, b)
    assert(x.size==1)
    assert(x(0)==5.0)
  }

  test("Simplest3") {
    val A = Map(
      (0,0)-> 1.0,
      (1,0)-> 3.0
    )
    val b = List(5.0, 15.0)
    val x: List[Double] = LSMRSolver.solve(A,2, 1, b)
    assert(x.size==1)
    assert(x(0)==5.0)
  }
  test("Simplest4") {
    val A = Map(
      (0,0)-> 1.0, (0,1)->5.0,
      (1,0)-> 3.0, (1,1)->2.0
    )
    val b = List(-5.0, 11.0)
    val x: List[Double] = LSMRSolver.solve(A,2, 2, b)
    assert(x.size==2)
    println(x)
    assert(math.abs(x(0)-5.0)<0.00001)
    assert(math.abs(x(1)- -2.0)<0.00001)
  }
  test("CreateMatrix") {
    val A = Map(
      (0,0)-> 1.0, (0,1)->5.0,
      (1,0)-> 3.0, (1,1)->2.0
    )
    val m: SparseMatrix = LSMRSolver.createRASparseMatrix(A, 2, 2)
    assert(m.numCols()==2)
    assert(m.numRows()==2)
    assert(m.get(0,0)==1.0)
    assert(m.get(0,1)==5.0)
    assert(m.get(1,0)==3.0)
    assert(m.get(1,1)==2.0)
  }
}
