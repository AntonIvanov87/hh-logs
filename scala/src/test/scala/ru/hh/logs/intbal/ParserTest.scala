package ru.hh.logs.intbal

import org.scalatest.FlatSpec
import ru.hh.logs.intbal.Parser.parse

class ParserTest extends FlatSpec {

  "parse" should "parse response code" in {
    val intbalLogLine1 = parse("1428044702.264 200 HIT [-] {-} {-} 192.168.1.184 GET /rs/language/all 0.004 {-}").get
    assertResult("200")(intbalLogLine1.responseCode)

    val intbalLogLine2 = parse("1428044703.400 400 HIT [-] {-} {-} 192.168.1.184 GET /rs/language/all 0.004 {-}").get
    assertResult("400")(intbalLogLine2.responseCode)
  }

  it should "parse cache status" in {
    val intbalLogLine1 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount.mvc 0.031 {-}").get
    assertResult("MISS")(intbalLogLine1.cacheStatus)

    val intbalLogLine2 = parse("1428044702.262 200 EXPIRED [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount.mvc 0.031 {-}").get
    assertResult("EXPIRED")(intbalLogLine2.cacheStatus)
  }

  it should "parse caller IP" in {
    val intbalLogLine1 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount.mvc 0.031 {-}").get
    assertResult("192.168.1.75")(intbalLogLine1.callerIP)

    val intbalLogLine2 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.76 GET /employer/allResumeSearchUsersCount.mvc 0.031 {-}").get
    assertResult("192.168.1.76")(intbalLogLine2.callerIP)
  }

  it should "parse HTTP method" in {
    val intbalLogLine1 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount.mvc 0.031 {-}").get
    assertResult("GET")(intbalLogLine1.httpMethod)

    val intbalLogLine2 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 POST /employer/allResumeSearchUsersCount.mvc 0.031 {-}").get
    assertResult("POST")(intbalLogLine2.httpMethod)
  }

  it should "parse pathAndQuery" in {
    val intbalLogLine1 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allVacancySearchUsersCount 0.031 {-}").get
    assertResult("/employer/allVacancySearchUsersCount")(intbalLogLine1.pathAndQuery)

    val intbalLogLine2: IntbalLine = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount 0.031 {-}").get
    assertResult("/employer/allResumeSearchUsersCount")(intbalLogLine2.pathAndQuery)
  }

  it should "parse duration" in {
    val intbalLogLine1 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount 0.031 {-}").get
    assertResult(0.031f)(intbalLogLine1.duration)

    val intbalLogLine2 = parse("1428044702.262 200 MISS [0.030] {127.0.0.1:8690} {200} 192.168.1.75 GET /employer/allResumeSearchUsersCount 0.032 {-}").get
    assertResult(0.032f)(intbalLogLine2.duration)
  }
}
