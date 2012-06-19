package com.fijimf.deepfij.view.user

import com.fijimf.deepfij.modelx.User

object UserListPanel {
  def apply(users: List[User]) {
    <div>
      <table>
        <thead>
          <tr>
          </tr>
        </thead>
        <tbody>
          {users.map(u => {
          <tr>
            <td>
              {u.getId()}
            </td>
            <td>
              {u.getEmail()}
            </td>
            <td>
              {u.getRoles}
            </td>
            <td>
              {"EDIT"}
            </td>
            <td>
              {"DELETE"}
            </td>
          </tr>
        })
        <tr>
          <td></td>
        </tr>}
        </tbody>
      </table>
    </div>
  }

}
