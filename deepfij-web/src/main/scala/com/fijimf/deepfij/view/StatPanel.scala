package com.fijimf.deepfij.view

import xml.NodeSeq


object StatPanel {
  def apply(key: String): NodeSeq = {
    <div>
      <div id="statTarget"></div>
    </div>
  }
}