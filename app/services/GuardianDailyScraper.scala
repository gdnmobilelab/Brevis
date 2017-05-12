package services

import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

/**
  * Created by connor.jennings on 3/20/17.
  */

class GuardianDailyScraper {
  val url = "https://www.theguardian.com/email/us/daily"
  val guardianBaseUrl = "https://www.theguardian.com/"

  def retrieveArticleIds(): List[String] = {
    try {
      val doc = Jsoup.connect(url).get()
      val articleAnchors: Elements = doc.select(".headline a.fc-link")

      articleAnchors.asScala.toList.map(anchor => {
        anchor.attr("href").replace(guardianBaseUrl, "")
      })
    } catch {
      case e: Exception =>
        e.printStackTrace()
        List()
    }
  }
}
