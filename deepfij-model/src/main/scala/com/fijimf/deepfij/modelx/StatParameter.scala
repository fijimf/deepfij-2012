package com.fijimf.deepfij.modelx

import javax.persistence._
import scala.Array
import scala.annotation.target.field
import java.util.Date
import com.fijimf.deepfij.statx.StatInfo


@Entity
@Table(name = "statParameter",
  uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("metaStat_id", "keyName", "date")))
)
class StatParameter(
                     @(Id@field)
                     @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
                     @(Column@field)(name = "id")
                     val id: Long = 0L,

                     @(ManyToOne@field)(optional = false)
                     @(JoinColumn@field)(name = "metaStat_id")
                     val metaStat: MetaStat = null,

                     @(Column@field)(name = "keyName")
                     val key: String = null,

                     @(Column@field)(name = "name")
                     val name: String = null,

                     @(Column@field)(name = "date")
                     @(Temporal@field)(value = TemporalType.DATE)
                     val date: Date = new Date(),

                     @(Column@field)(name = "value")
                     val value: Double = 0.0
                     ) {
  def this() = this(0L)


}

class StatParameterDao extends BaseDao[StatParameter, Long] {

}
