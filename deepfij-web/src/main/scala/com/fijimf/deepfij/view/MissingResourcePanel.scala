package com.fijimf.deepfij.view

import java.text.SimpleDateFormat
import com.fijimf.deepfij.modelx.Conference


object MissingResourcePanel {

  def apply(resource: String, value: String) = {
    <div class="row">
      <div class="span12">
        <h1>
          Resource Not Found
        </h1>
      </div>
    </div>
      <div class="row">
      <div class="span12">
        <br/>
        <div class="alert alert-error">
          {"The " + resource + " keyed by the value '" + value + "' could not be found."}
        </div>
      </div>
    </div>
  }
}