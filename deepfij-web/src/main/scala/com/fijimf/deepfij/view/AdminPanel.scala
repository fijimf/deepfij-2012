package com.fijimf.deepfij.view

import com.fijimf.deepfij.server.Util._


object AdminPanel {
  def apply() = {
    <h1>Admin</h1>
      <div class="row">
        <div class="span12">
          {createAccordian(
          "admin-accordian",
          List(
            ("collapseSchedules", <h3>Schedules</h3>, ScheduleListPanel()),
            ("collapseUsers", <h3>Users</h3>, <p>Lorem</p>),
            ("collapseQuotes", <h3>Quotes</h3>, <p>Lorem</p>)
          ),
          true)}
        </div>
      </div>
      <script src="/scripts/popaccordion.js"></script>
  }
}
