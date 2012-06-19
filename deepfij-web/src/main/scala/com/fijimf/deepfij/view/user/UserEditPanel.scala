package com.fijimf.deepfij.view.user

import com.fijimf.deepfij.modelx.User

object UserEditPanel {

  def apply(u: User) = {
    <div>
      <h3>Change email</h3>
      <h3>Reset password</h3>
      <h3>Add/Delete roles</h3>
      <h3>Delete</h3>
    </div>

  }

}
