package ru.hh.logs.intbal

import ru.hh.logs.intbal.Parser.parse
import ru.hh.logs.{Resource, UrlNormalizer}

import scala.io.Source._

object ResourcesMetrics {

  def main(args: Array[String]) {
    val requestsMetrics = resourcesMetricsFromFiles(args, _.cacheStatus != "HIT").toList
    printHeader()
    requestsMetrics.sortBy(_.totalDurationPercent).foreach(printRequestMetrics)
  }

  private[intbal] def resourcesMetricsFromFiles(files: Traversable[String], lineFilter: (IntbalLine => Boolean)): Traversable[ResourceMetrics] = {

    val resourceToRawMetrics = files
      .map(requestToRawMetricsFromFile(_, lineFilter))
      .aggregate(Map[Resource, RawMetrics]())(mergeRequestToMetrics, mergeRequestToMetrics)

    val allRawMetrics = resourceToRawMetrics.values
    val totalCount = allRawMetrics.map(_.count).sum
    val totalUpDuration = allRawMetrics.map(_.totalDuration).sum

    resourceToRawMetrics
      .map { case (request, rawMetrics) => toResourceMetrics(request, rawMetrics, totalCount, totalUpDuration) }
  }

  private def requestToRawMetricsFromFile(file: String, lineFilter: (IntbalLine => Boolean)): Map[Resource, RawMetrics] = {
    fromFile(file)
      .getLines()
      .map(parse)
      .filter(_.isSuccess)
      .map(_.get)
      .filter(lineFilter)
      .map(intbalLine => (toNormalizedRequest(intbalLine), RawMetrics(intbalLine)))
      .aggregate(Map[Resource, RawMetrics]())(mergeRequestToMetrics, mergeRequestToMetrics)
  }

  private def mergeRequestToMetrics(requestToMetrics: Map[Resource, RawMetrics], requestAndMetrics: Pair[Resource, RawMetrics]): Map[Resource, RawMetrics] = {
    val oldMetrics = requestToMetrics.get(requestAndMetrics._1)
    val newMetrics = if (oldMetrics.isEmpty) requestAndMetrics._2 else oldMetrics.get + requestAndMetrics._2
    requestToMetrics + (requestAndMetrics._1 -> newMetrics)
  }

  private def mergeRequestToMetrics(requestToMetrics1: Map[Resource, RawMetrics], requestToMetrics2: Map[Resource, RawMetrics]): Map[Resource, RawMetrics] = {
    requestToMetrics2.foldLeft(requestToMetrics1)(mergeRequestToMetrics)
  }

  private def toNormalizedRequest(intbalLine: IntbalLine): Resource = {
    val normalizedPath = UrlNormalizer.normalize(intbalLine.pathAndQuery)
    Resource(intbalLine.httpMethod, normalizedPath)
  }

  private case class RawMetrics(count: Long, totalDuration: Double, hits: Long) {
    def +(that: RawMetrics): RawMetrics = {
      RawMetrics(
        this.count + that.count,
        this.totalDuration + that.totalDuration,
        this.hits + that.hits
      )
    }
  }

  private object RawMetrics {
    def apply(intbalLine: IntbalLine): RawMetrics = {
      val hits = if (intbalLine.cacheStatus == "HIT") 1 else 0
      RawMetrics(1, intbalLine.duration, hits)
    }
  }

  private def toResourceMetrics(resource: Resource, rawMetrics: RawMetrics, totalCount: Long, totalDuration: Double): ResourceMetrics = {
    ResourceMetrics(
      resource,
      (100.0 * rawMetrics.count / totalCount).toFloat,
      (100.0 * rawMetrics.totalDuration / totalDuration).toFloat,
      (rawMetrics.totalDuration / rawMetrics.count).toFloat,
      (100.0 * rawMetrics.hits / rawMetrics.count).toFloat
    )
  }

  private def printHeader() {
    val resource = "resource"
    println(f"$resource%-80s| count, %% | duration, %% | avg. duration, sec | hit %%")
  }

  private def printRequestMetrics(requestMetrics: ResourceMetrics) {
    println(f"${requestMetrics.resource}%-80s|" +
      f"${requestMetrics.countPercent}%5.2f|" +
      f"${requestMetrics.totalDurationPercent}%5.2f|" +
      f"${requestMetrics.avgDuration}%10.3f|" +
      f"${requestMetrics.hitPercent}%5.2f"
    )
  }
}
