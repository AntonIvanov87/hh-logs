package ru.hh.logs.intbal;

import ru.hh.IPToService;
import ru.hh.graph.GraphEdge;
import ru.hh.graph.GraphEdgeId;
import ru.hh.logs.GraphExtractor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;

public class IntbalGraph {

  // 02/May/2017:11:00:37 +0300 200 MISS [0.005] {192.168.1.221:8080} {200} 192.168.2.198 271 GET /vacancyresponses/canview?vacancyId=19043678 0.005 {1493712037080c429e8e0415348f5dd9
  private static final Pattern linePattern = Pattern.compile("^[^ ]++ [^ ]++ [0-9]++ [^ ]++ \\[[^]]++] \\{(?<upstreamIp>[0-9.-]++)[^}]*+} \\{[^}]++} (?<callerIp>[0-9.]++) [0-9]++ [^ ]++ [^ ]++ (?<timeSec>[0-9.]++) .++");

  public static void main(String[] args) throws IOException {
    GraphExtractor.EdgeFactory edgeFactory = (String line, String targetService) -> {
      if (line.length() > 8040) {
        return null;  // the line was most likely cut, ignoring
      }

      Matcher matcher = linePattern.matcher(line);
      if (!matcher.matches()) {
        throw new RuntimeException("Unknown line format: " + line);
      }

      if (matcher.group("upstreamIp").equals("-")) {
        return null;
      }
      String callerIp = matcher.group("callerIp");
      String callerService = IPToService.get(callerIp);
      Double timeSec = parseDouble(matcher.group("timeSec"));
      GraphEdgeId graphEdgeId = new GraphEdgeId(callerService, targetService);
      return new GraphEdge(graphEdgeId, 1, timeSec);
    };
    GraphExtractor.extract(args, edgeFactory);
  }

  private IntbalGraph() {
  }
}
