package ru.hh.logs.intbal

import java.util.regex.Pattern

import ru.hh.logs.{TooLongLineException, UnknownLineFormatException}

import scala.util.{Failure, Success, Try}

private[intbal] object Parser {

  private val maxLineLength = 8000

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

  private[intbal] def parse(fileLine: String): Try[IntbalLine] = {
    if (fileLine.length > maxLineLength) {
      return Failure(new TooLongLineException(fileLine))
    }
    val matcher = fileLinePattern.matcher(fileLine)
    if (!matcher.find) {
      throw new UnknownLineFormatException(fileLine)
    }
    val responseCode: String = matcher.group("responseCode")
    val cacheStatus: String = matcher.group("cacheStatus")
    val callerIP: String = matcher.group("callerIP")
    val httpMethod: String = matcher.group("httpMethod")
    val pathAndQuery: String = matcher.group("pathAndQuery")
    val duration: Float = matcher.group("duration").toFloat
    Success(
      IntbalLine(callerIP, httpMethod, pathAndQuery, cacheStatus, responseCode, duration)
    )
  }
}
