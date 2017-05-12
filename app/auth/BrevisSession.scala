package auth

/**
  * Created by connor.jennings on 3/31/17.
  */
case class BrevisSession (
  id: String,
  creationTime: Long,
  lastAccessTime: Long,
  maxInactiveTime: Long,
  sessionData: Object
)
