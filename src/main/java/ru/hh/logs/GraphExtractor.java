package ru.hh.logs;

import ru.hh.graph.GraphEdge;
import ru.hh.graph.GraphEdgeId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Comparator.comparingInt;

public class GraphExtractor {

  @FunctionalInterface
  public interface EdgeFactory {
    GraphEdge fromLine(String line, String service);
  }

  public static void extract(String[] files, EdgeFactory edgeFactory) throws IOException {
    long start = currentTimeMillis();

    Map<GraphEdgeId, GraphEdge> edgeIdToEdge = new HashMap<>();
    for(String fileName : files) {
      processFile(fileName, edgeFactory, edgeIdToEdge);
    }
    printEdges(edgeIdToEdge.values());

    System.out.println("finished in " + (currentTimeMillis() - start) + " ms");
  }

  private static void processFile(String fileName, EdgeFactory edgeFactory, Map<GraphEdgeId, GraphEdge> edgeIdToEdge) throws IOException {
    Path path = Paths.get(fileName);

    String shortFileName = path.getFileName().toString();
    String service = shortFileName.substring(0, shortFileName.indexOf('.'));

    Map<Thread, Map<GraphEdgeId, GraphEdge>> threadToEdgeIdToEdge = new ConcurrentHashMap<>();

    Files.lines(path).parallel().forEach(line -> {
      GraphEdge edge = edgeFactory.fromLine(line, service);
      if (edge != null) {
        Thread currentThread = Thread.currentThread();
        Map<GraphEdgeId, GraphEdge> threadEdgeIdToEdge = threadToEdgeIdToEdge.computeIfAbsent(currentThread, k -> new HashMap<>());
        mergeEdgeToMap(edge, threadEdgeIdToEdge);
      }
    });

    for (Map<GraphEdgeId, GraphEdge> threadEdgeIdToEdge : threadToEdgeIdToEdge.values()) {
      for (GraphEdge edge : threadEdgeIdToEdge.values()) {
        mergeEdgeToMap(edge, edgeIdToEdge);
      }
    }
  }

  private static void mergeEdgeToMap(GraphEdge edge, Map<GraphEdgeId, GraphEdge> edgeIdToEdge) {
    GraphEdge edgeFromMap = edgeIdToEdge.get(edge.id);
    if (edgeFromMap == null) {
      edgeIdToEdge.put(edge.id, edge);
    } else {
      edgeFromMap.numOfCalls += edge.numOfCalls;
      edgeFromMap.seconds += edge.seconds;
    }
  }

  private static void printEdges(Collection<GraphEdge> edges) {
    List<GraphEdge> listOfEdges = new ArrayList<>(edges);
    listOfEdges.sort(comparingInt(graphEdge -> graphEdge.numOfCalls));
    for (GraphEdge edge : listOfEdges) {
      System.out.println(format(
          "%s -> %s: %d calls, %.0f sec",
          edge.id.callerService, edge.id.targetService, edge.numOfCalls, edge.seconds
      ));
    }
  }
}
