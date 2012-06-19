package com.fijimf.deepfij.view

import xml.NodeSeq


object StatPanel {
  def apply(key: String): NodeSeq = {
    <div>
      <div id="stat">
          <h3 id="name"/>
          <h3 id="mean"/>
          <h3 id="stddev"/>
          <div id="shit"/>
      </div>
    </div>
  }
}