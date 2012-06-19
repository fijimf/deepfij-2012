package com.fijimf.deepfij.view.user

import com.fijimf.deepfij.modelx.User

object UserListPanel {
  def apply(users: List[User]) = {
    <div class="row">
      <div class="span8">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Email</th>
              <th>Roles</th>
                <th/>
            </tr>
          </thead>
          <tbody>
            {users.map(u => {
            <tr>
              <td>
                {u.id}
              </td>
              <td>
                {u.email}
              </td>
              <td>
                {u.roleList.mkString(", ")}
              </td>
              <td>
                <a href={"/user/edit/" + u.id}>Edit</a>
              </td>
            </tr>
          })}
          </tbody>
        </table>
      </div>
    </div>
  }

}
