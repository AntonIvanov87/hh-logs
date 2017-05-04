package ru.hh.graph;

public class GraphEdge {

  public final GraphEdgeId id;
  public int numOfCalls;
  public double seconds;

  public GraphEdge(GraphEdgeId id, int numOfCalls, double seconds) {
    this.id = id;
    this.numOfCalls = numOfCalls;
    this.seconds = seconds;
  }
}
