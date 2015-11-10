package ru.hh.logs;

public class NormalizedRequest {

  public final String method;
  public final String normalizedUrl;

  public NormalizedRequest(final String method, final String normalizedUrl) {
    this.method = method;
    this.normalizedUrl = normalizedUrl;
  }
}
