package com.fijimf.deepfij.view

import xml.NodeSeq


object StatPanel {
  def apply(key: String): NodeSeq = {
    <div class="row">
      <div class="span12">
        <h1 id="statName" />
      </div>
    </div>
    <div class="row">
      <div class="span4">
        <h3 id="statMean">&mu; = </h3>
      </div>
      <div class="span4" >
        <h3 id="statStdDev">&sigma; = </h3>
      </div>
    </div>
    <div class="row">
      <div class="span12">
        <div id="chart">

        </div>
      </div>
    </div>
  }
}