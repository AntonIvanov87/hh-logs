package ru.hh.logs.intbal

import java.nio.file.Files.{createTempFile, delete, write}

import org.scalatest.{Matchers, FlatSpec}

import scala.collection.JavaConversions.asJavaIterable

class ResourcesMetricsTest extends FlatSpec with Matchers {

  // TODO: test
//  "resourcesMetricsFromFiles" should "return requests metrics" in {
//    val lines = asJavaIterable(List(
//      "1428044702.264 200 HIT [-] {-} {-} 192.168.1.184 GET /some/path1 0.1 {-}",
//      "1428044702.264 200 MISS [-] {-} {-} 192.168.1.184 POST /some/path2 0.3 {-}"
//    ))
//
//    val tempFile1 = createTempFile("requestsMetricsFromFilesTest", null)
//    val tempFile2 = createTempFile("requestsMetricsFromFilesTest", null)
//    var requestsMetrics: Traversable[ResourceMetrics] = null
//    try {
//      write(tempFile1, lines)
//      write(tempFile2, lines)
//
//      requestsMetrics = resourcesMetricsFromFiles(List(tempFile1.toString, tempFile2.toString), _ => true)
//
//    } finally {
//      delete(tempFile1)
//      delete(tempFile2)
//    }
//
//    val somePath1Metrics = requestsMetrics.find(_.resource.path == "/some/path1").get
//    somePath1Metrics.resource.method should equal("GET")
//    somePath1Metrics.countPercent should equal (50.0f +- 0.001f)
//    somePath1Metrics.totalDurationPercent should equal (25f +- 0.001f)
//    somePath1Metrics.hitPercent should equal (100f +- 0.001f)
//
//    val somePath2Metrics = requestsMetrics.find(_.resource.path == "/some/path2").get
//    somePath2Metrics.resource.method should equal ("POST")
//    somePath2Metrics.countPercent should equal (50.0f +- 0.001f)
//    somePath2Metrics.totalDurationPercent should equal (75f +- 0.001f)
//    somePath2Metrics.hitPercent should equal (0f +- 0.001f)
//  }
}
