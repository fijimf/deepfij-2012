package com.fijimf.deepfij.slick.util

import slick.lifted.MappedTypeMapper
import java.sql.Date
import org.joda.time.{DateMidnight, DateTime}
import slick.lifted.TypeMapper.DateTypeMapper


object DateMidnightMapper {

    implicit def date2dateTime = MappedTypeMapper.base[DateMidnight, Date] (
      dateTime => new Date(dateTime.getMillis),
      date => new DateMidnight(date)
    )

}
