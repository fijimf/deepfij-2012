package com.fijimf.deepfij.nn

import breeze.linalg.{DenseVector, DenseMatrix}

class Node {
  def propagate() = {

  }

}

case class Layer(theta: DenseMatrix[Double]) {

  def numInputs: Int = theta.numCols

  def numOutputs: Int = theta.numRows

  def calc(inputs:DenseVector[Double]) :Double = {
    0.0
  }
}


