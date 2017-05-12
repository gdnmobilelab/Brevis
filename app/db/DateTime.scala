package db

import java.time.LocalTime

import anorm.{Column, ToStatement, TypeDoesNotMatch}

/**
  * Created by connor.jennings on 4/3/17.
  */
object DateTime {
  implicit def rowToLocalTime: Column[LocalTime] = Column.nonNull { (value, _) =>
    value match {
      case t: java.sql.Time => Right(t.toLocalTime)
      case str: java.lang.String => Right(LocalTime.parse(str))
      case x => Left(TypeDoesNotMatch(x.getClass.toString))
    }
  }

  implicit def localTimeToStatement = new ToStatement[LocalTime] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: LocalTime) {
      s.setObject(index, aValue)
    }
  }
}
