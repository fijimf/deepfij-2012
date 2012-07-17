package com.fijimf.deepfij.view.team

import com.fijimf.deepfij.modelx.{ConferenceDao, Team}
import xml.NodeSeq
import java.text.SimpleDateFormat

object TeamEditPanel {
  val fmt = new SimpleDateFormat("M/d/yyyy")
  val cdao= new ConferenceDao
  def apply(team: Option[Team]): NodeSeq = {
    <h1>New Team</h1>
    <div class="row">
      <div class="span12">
        <form class="well">
          <label>Name</label>
          <input type="text" class="span3" placeholder="Name" />
          <label>Long Name</label>
          <input type="text" class="span5" placeholder="Long Name" />
          <label>Key</label>
          <input type="text" class="span3" placeholder="key" />
          <label>Nickname</label>
          <input type="text" class="span3" placeholder="Nickname" />
          <label>Conference</label>
          <select>
            {cdao.findAll().map(c=>{
               <option>{c.name}</option>
            })}
          </select>
          <label>Primary Color</label>
          <input type="text" class="colorpicker" value="#8fff00" id="cp1"/>
          <label>Secondary Color</label>
          <input type="text" class="colorpicker" value="#8fff00" id="cp2"/>
          <button type="submit" class="btn">Submit</button>
        </form>
      </div>
    </div>
    <script>
      {"""$(function(){
    			$('.colorpicker').colorpicker({
    				format: 'hex'
    			});
    		});
       """}
    </script>
  }

}
