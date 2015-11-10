package intbal

import (
  "runtime"
  "fmt"
)

func ProcessLines(lines <-chan string) {
  results := make([]chan map[resource]*rawMetrics, runtime.NumCPU())
  for i := range results {
    resourceToRawMetrics := make(chan map[resource]*rawMetrics, 1)
    go processLinesFromChan(lines, resourceToRawMetrics)
    results[i] = resourceToRawMetrics
  }

  resourceToRawMetrics := make(map[resource]*rawMetrics)
  for _, chanResourceToRawMetrics := range results {
    mergeResourceToRawMetrics(<- chanResourceToRawMetrics, resourceToRawMetrics)
  }

  for resource, rawMetrics := range resourceToRawMetrics {
    fmt.Printf("%s %s %d %.2f\n", resource.method, resource.path, rawMetrics.count, rawMetrics.totalDurationSec)
  }
}

type rawMetrics struct {
  count            int
  totalDurationSec float64
}

func processLinesFromChan(rawLines <-chan string, result chan<- map[resource]*rawMetrics) {
  resourceToRawMetrics := make(map[resource]*rawMetrics)
  for rawLine := range rawLines {
    line, err := parse(rawLine)
    if err != nil {
      continue
    }

    // TODO: extract to function
    //if line.cacheStatus == "HIT" {
      // continue
    //}
    mergeLineToResourceToRawMetrics(line, resourceToRawMetrics)
  }
  result <- resourceToRawMetrics
}

func mergeLineToResourceToRawMetrics(line *line, resourceToRawMetrics map[resource]*rawMetrics) {
  resource := resource{line.httpMethod, normalize(line.pathAndQuery)}
  resourceRawMetrics, ok := resourceToRawMetrics[resource]
  if (!ok) {
    resourceRawMetrics = &rawMetrics{}
    resourceToRawMetrics[resource] = resourceRawMetrics
  }
  resourceRawMetrics.count += 1
  resourceRawMetrics.totalDurationSec += float64(line.durationSec)
}

func mergeResourceToRawMetrics(from map[resource]*rawMetrics, to map[resource]*rawMetrics) {
  for resource, fromRawMetrics := range from {
    toRawMetrics, ok := to[resource]
    if (!ok) {
      toRawMetrics = &rawMetrics{}
      to[resource] = toRawMetrics
    }
    toRawMetrics.count += fromRawMetrics.count
    toRawMetrics.totalDurationSec += fromRawMetrics.totalDurationSec
  }
}
