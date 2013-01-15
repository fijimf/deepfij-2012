package com.fijimf.deepfij.statx.models

import org.apache.mahout.classifier.sgd.{L1, PriorFunction, OnlineLogisticRegression}
import java.io.{DataOutput, DataInput}

class LogisticRegression {
  new OnlineLogisticRegression(2,347,new L1())

}
