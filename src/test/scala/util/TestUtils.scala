package util

import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsNull, JsValue}
import play.api.libs.ws.WSClient
import org.scalatest.{AsyncFlatSpec, Matchers}
import com.typesafe.config.{Config, ConfigFactory}

trait TestUtils extends AsyncFlatSpec with Matchers with Status {

  protected val application: Application = new GuiceApplicationBuilder().build
  protected val ws: WSClient = application.injector.instanceOf(classOf[WSClient])
  protected val request = new RequestGenerator(ws)
  protected val config: Config = ConfigFactory.load()

  protected val biBaseUrl: String = config.getString("api.bi.base.url")

  protected val legalUnit1: Int = config.getInt("data-item.legal.unit.1")
  
  protected val expectedTradingStatus: String = config.getString("data-item.expected.trading.status")
  protected val expectedLegalStatus1: String = config.getString("data-item.expected.legal.status")
  protected val expectedLegalUnit1: Int = config.getInt("data-item.expected.legal.unit.1")



  implicit class orElseNull(js: Option[JsValue]) {
    def getOrNull: JsValue = {
      js match {
        case Some(null) => JsNull
        case Some(j: JsValue) => j
        case None => sys.error("Unexpected match! Found None type -> failure")
      }
    }
  }

}
