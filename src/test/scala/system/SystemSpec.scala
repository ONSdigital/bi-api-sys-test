package system

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.JsValue

import util._

class SystemSpec extends TestUtils {

  private val UNIT_LIST = List("VAT", "PAYE", "LEU", "CH", "ENT")
  private val REFERENCE_PERIOD_FORMAT = "yyyyMM"
  private val DELIMITER = "-"
  private val EXPECTED_API_CONTENT_TYPE = "application/json"
  private val EXPECTED_DOCS_CONTENT_TYPE = "text/html; charset=utf-8"

  private val BUSINESS_INDEX = "bi-api"


  // TODO - fix children and parents testing null values

  behavior of BUSINESS_INDEX

  /**
    * GET REQUESTS
    */
  "A vat search" should "return a found ubrn record (only) without additional unit details" taggedAs(Search, VAT) in {
    request.singleGETRequest(s"$biBaseUrl/v1/search?query=_id:$legalUnit1").map{ resp =>
      resp.json.as[Seq[JsValue]].nonEmpty
      (resp.json.as[Seq[JsValue]].head \ "id").as[Int] shouldEqual expectedLegalUnit1
      (resp.json.as[Seq[JsValue]].head \ "tradingStatus").as[String] shouldEqual expectedTradingStatus
      resp.status shouldEqual OK
      resp.header("Content-Type") shouldEqual Some(EXPECTED_API_CONTENT_TYPE)
    }
  }

  "A ubrn search" should "return the legal unit record with any child unit(s)" taggedAs(Search, LegalUnit) in {
    request.singleGETRequest(s"$biBaseUrl/v1/business/$legalUnit1").map{ resp =>
      (resp.json.as[JsValue] \ "id").as[Int] shouldEqual legalUnit1
      val l = (resp.json.as[JsValue] \ "vatRefs").as[List[Int]]
      l.nonEmpty shouldEqual true
      l.contains(10000) shouldEqual true
      (resp.json.as[JsValue] \ "legalStatus").as[String] shouldEqual expectedLegalStatus1
    }
  }

  "Version controller request for sbr-admin-data" should "GET a version listing" taggedAs Util in {
    request.singleGETRequest(s"$biBaseUrl/version").map { resp =>
      (resp.json \ "name").as[String] startsWith "sbr-admin-data"
      (resp.json \ "scalaVersion").as[String] startsWith "2."
      (resp.json \ "version").as[String] endsWith "-SNAPSHOT"
      //      (resp.json \ "apiVersion").as[String] endsWith "-SNAPSHOT"
      resp.status shouldEqual OK
      resp.header("Content-Type") shouldEqual Some(EXPECTED_API_CONTENT_TYPE)
    }
  }

  "Health controller request for sbr-admin-data" should "GET a healthy status of the api" taggedAs Util in {
    request.singleGETRequest(s"$biBaseUrl/health").map { resp =>
      (resp.json \ "Status").as[String] shouldEqual "Ok"
      resp.status shouldEqual OK
      resp.header("Content-Type") shouldEqual Some(EXPECTED_API_CONTENT_TYPE)
    }
  }

  "Hitting the swagger docs route request for sbr-admin-data" should "GET the swagger doc ui to test " +
    "endpoints" taggedAs Util in {
    request.singleGETRequest(s"$biBaseUrl/docs").map { resp =>
      resp.status shouldEqual OK
      resp.header("Content-Type") shouldEqual Some(EXPECTED_DOCS_CONTENT_TYPE)
    }
  }

}
