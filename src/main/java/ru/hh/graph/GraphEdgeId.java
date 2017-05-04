package ru.hh.graph;

public class GraphEdgeId {
  public final String callerService;
  public final String targetService;

  public GraphEdgeId(String callerService, String targetService) {
    this.callerService = callerService;
    this.targetService = targetService;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GraphEdgeId that = (GraphEdgeId) o;

    return (callerService.equals(that.callerService)
        && targetService.equals(that.targetService));
  }

  @Override
  public int hashCode() {
    int result = callerService.hashCode();
    result = 31 * result + targetService.hashCode();
    return result;
  }
}
