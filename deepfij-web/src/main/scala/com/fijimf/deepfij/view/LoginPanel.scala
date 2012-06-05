package com.fijimf.deepfij.view


object LoginPanel {

  def apply() = {
    <div class="row">
      <div class="span12">
        <h1>
          Deep Fij Login
        </h1>
      </div>
    </div>
      <div class="row">
        <div class="span12">
          <form class="well" method="post" >
            <label>Email</label>
              <input id="email" name="email" type="text" class="span3" placeholder="user@email.com"/>
            <label class="password">
              <input id="password" name="password" type="password" class="span3"/>
            </label>
            <button type="submit" class="btn">Login</button>

          </form>
        </div>
      </div>
  }
}