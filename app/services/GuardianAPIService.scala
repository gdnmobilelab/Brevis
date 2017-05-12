package services

import com.google.inject.Inject
import com.gu.contentapi.client.GuardianContentClient
import com.gu.contentapi.client.model.ItemQuery
import com.gu.contentapi.client.model.v1.ItemResponse
import play.api.Configuration

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  * Created by connor.jennings on 3/17/17.
  */
class GuardianAPIService @Inject() (configuration: Configuration) {
  val client = new GuardianContentClient(configuration.getString("brevis.contentAPIKey").get)

  def getItem(id: String): Future[ItemResponse] = {
    val itemQuery = ItemQuery(id)
                      .showBlocks("all")
                      .showFields("all")
                      .showTags("all")

    client.getResponse(itemQuery)
  }
}
