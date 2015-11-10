package ru.hh.logs.intbal

import java.util.regex.Pattern

import scala.util.{Failure, Success, Try}

private[intbal] object FileLineParser {

  private[intbal] def parse(fileLine: String): Try[IntbalLine] = {
    if (fileLine.length > 8000) {
      return Failure(new RuntimeException("too long line: " + fileLine.substring(0, 50) + "..."))
    }
    val matcher = fileLinePattern.matcher(fileLine)
    if (!matcher.find) {
      throw new RuntimeException("Unknown line format: " + fileLine)
    }
    Success(
      IntbalLine(
        matcher.group("callerIP"),
        matcher.group("httpMethod"),
        matcher.group("pathAndQuery"),
        matcher.group("cacheStatus"),
        matcher.group("responseCode"),
        matcher.group("duration").toFloat
      )
    )
  }

  private val fileLinePattern = Pattern.compile(
    "^(?<timestamp>[0-9]+\\.[0-9]+) " +
      "(?<responseCode>[0-9]+) " +
      "(?<cacheStatus>[a-zA-Z-]+) " +
      "\\[(?<upstreamsDurations>[0-9., -]+)\\] " +
      "\\{(?<upstreamsIPsAndPorts>[0-9.:, -]+)\\} " +
      "\\{(?<upstreamsResponsesCodes>[0-9, -]+)\\} " +
      "(?<callerIP>[0-9.]+) " +
      "(?<httpMethod>[a-zA-Z]+) " +
      "(?<pathAndQuery>\\S+) " +
      "(?<duration>[0-9]+\\.[0-9]+) " +
      // sometimes request id is strangely duplicated
      "\\{(?<requestId>[0-9a-zA-Z-]+)[,0-9a-z]*\\}")
}
