package ru.hh.logs.frontik;

import ru.hh.IPToService;
import ru.hh.IntbalPortToService;
import ru.hh.graph.GraphEdge;
import ru.hh.graph.GraphEdgeId;
import ru.hh.logs.GraphExtractor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;

public class FrontikGraph {

  // [21] 2017-05-02 11:00:43,427 INFO frontik.handler.xhh.pages.related_resumes.Page.1493712043402897c8015b645f977d85: got 200 1779 bytes http://10.10.10.1:8669/currencyList?replicaOnlyRq=true&site=1 in 1.82ms
  private static final Pattern linePattern = Pattern.compile("^[^ ]++ [^ ]++ [^ ]++ INFO [^ ]++ got [0-9]++ [0-9]++ bytes [^:]++://(?<targetIP>[^:]++):(?<targetPort>[0-9]++)[^ ]++ in (?<timeMs>[0-9.]++)ms");

  public static void main(String[] args) throws IOException {
    GraphExtractor.EdgeFactory edgeFactory = (String line, String callerService) -> {
      Matcher matcher = linePattern.matcher(line);
      if (!matcher.matches()) {
        return null;
      }

      String targetIP = matcher.group("targetIP");
      String targetService;
      if (targetIP.equals("10.10.10.1")) {
        String targetPort = matcher.group("targetPort");
        targetService = IntbalPortToService.get(targetPort);
      } else {
        targetService = IPToService.get(targetIP);
      }
      Double timeMs = parseDouble(matcher.group("timeMs"));
      GraphEdgeId graphEdgeId = new GraphEdgeId(callerService, targetService);
      return new GraphEdge(graphEdgeId, 1, timeMs / 1000);
    };
    GraphExtractor.extract(args, edgeFactory);
  }

  private FrontikGraph() {
  }
}
