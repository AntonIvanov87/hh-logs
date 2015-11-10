package ru.hh.logs.intbal

object Main {

  def main(args: Array[String]) {
    val resourcesMetrics = ResourcesMetrics.fromStdIn(_.cacheStatus != "HIT").toList
    printHeader()
    resourcesMetrics.sortBy(_.totalDurationPercent).foreach(printResourceMetrics)
  }

  private def printHeader() {
    println("count, % | duration, % | avg. duration, sec | hit, % | resource")
  }

  private def printResourceMetrics(resourceMetrics: ResourceMetrics) {
    if (resourceMetrics.countPercent < 1 && resourceMetrics.totalDurationPercent < 1) {
      return
    }
    println(f"${resourceMetrics.countPercent}%8.2f | " +
          f"${resourceMetrics.totalDurationPercent}%11.2f | " +
          f"${resourceMetrics.avgDuration}%18.3f | " +
          f"${resourceMetrics.hitPercent}%6.2f | " +
          f"${resourceMetrics.resource}"
    )
  }
}
