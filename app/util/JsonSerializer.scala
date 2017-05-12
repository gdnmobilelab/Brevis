package util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Created by connor.jennings on 3/20/17.
  */
object JsonSerializer {
  def apply() = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)

    mapper
  }
}
