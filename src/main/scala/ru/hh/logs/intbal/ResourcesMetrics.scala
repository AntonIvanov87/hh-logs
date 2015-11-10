package ru.hh.logs.intbal

import java.io.{BufferedReader, InputStreamReader}

import ru.hh.logs.intbal.FileLineParser.parse
import ru.hh.logs.{Resource, UrlNormalizer}

object ResourcesMetrics {

  private[intbal] def fromStdIn(lineFilter: (IntbalLine => Boolean)): Iterable[ResourceMetrics] = {

    val resourceToRawMetrics = resourceToRawMetricsFromStdIn(lineFilter)

    val allRawMetrics = resourceToRawMetrics.values
    val totalCount = allRawMetrics.map(_.count).sum
    val totalUpDuration = allRawMetrics.map(_.totalDuration).sum

    resourceToRawMetrics
        .map { case (resource, rawMetrics) => toResourceMetrics(resource, rawMetrics, totalCount, totalUpDuration) }
  }

  private def resourceToRawMetricsFromStdIn(lineFilter: (IntbalLine => Boolean)): Map[Resource, RawMetrics] = {
    val stdInReader = new BufferedReader(new InputStreamReader(System.in))
    // TODO: futures
    val workers = (0 until Runtime.getRuntime.availableProcessors()).map(_ => new Worker(stdInReader, lineFilter))
    workers.foreach(_.start())
    workers.map(worker => {
      try {
        worker.join()
      } catch {
        case i: InterruptedException => throw new RuntimeException(i)
      }
      worker.resourceToRawMetrics
    }).foldLeft(Map[Resource, RawMetrics]())(mergeResourceToMetrics)
  }

  private class Worker(private val linesReader: BufferedReader, private val lineFilter: (IntbalLine => Boolean)) extends Thread {

    @volatile
    var resourceToRawMetrics: Map[Resource, RawMetrics] = null

    override def run() {
      resourceToRawMetrics = resourceToRawMetricsFromLinesReader(linesReader, lineFilter)
    }
  }

  private def resourceToRawMetricsFromLinesReader(lines: BufferedReader,
                                                  lineFilter: (IntbalLine => Boolean)): Map[Resource, RawMetrics] = {
    var resourceToRawMetrics = Map[Resource, RawMetrics]()
    while (true) {
      val line = lines.readLine()
      if (line == null) {
        return resourceToRawMetrics
      }
      val intbalLineTry = parse(line)
      if (intbalLineTry.isSuccess) {
        val intbalLine = intbalLineTry.get
        if (lineFilter(intbalLine)) {
          val normalizedRequest = toNormalizedRequest(intbalLine)
          val rawMetrics = RawMetrics(intbalLine)
          resourceToRawMetrics = mergeResourceToMetrics(resourceToRawMetrics, (normalizedRequest, rawMetrics))
        }
      }
    }
    resourceToRawMetrics
  }

  // XXX: overload +
  private def mergeResourceToMetrics(resourceToMetrics: Map[Resource, RawMetrics],
                                     resourceAndRawMetrics: (Resource, RawMetrics)): Map[Resource, RawMetrics] = {
    val (resource, rawMetrics) = resourceAndRawMetrics
    val oldMetrics = resourceToMetrics.get(resource)
    val newMetrics = if (oldMetrics.isEmpty) rawMetrics else oldMetrics.get + rawMetrics
    resourceToMetrics + (resource -> newMetrics)
  }

  // XXX: overload +
  private def mergeResourceToMetrics(resourceToRawMetrics1: Map[Resource, RawMetrics],
                                     resourceToRawMetrics2: Map[Resource, RawMetrics]): Map[Resource, RawMetrics] = {
    resourceToRawMetrics2.foldLeft(resourceToRawMetrics1)(mergeResourceToMetrics)
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
}
