package intbal

import (
  "fmt"
  "regexp"
  "strconv"
)

type line struct {
  callerIP     string
  httpMethod   string
  pathAndQuery string
  cacheStatus  string
  responseCode string
  durationSec  float32
}

func parse(rawLine string) (*line, error) {
  if len(rawLine) > maxLineLength {
    return nil, fmt.Errorf("too long line (> %d symbols)", maxLineLength)
  }

  groupToValue, err := groupToValue(rawLine)

  duration, err := strconv.ParseFloat(groupToValue["duration"], 32)
  if err != nil {
    return nil, fmt.Errorf("failed to convert duration '%s' to float", groupToValue["duration"])
  }

  return &line{groupToValue["callerIP"], groupToValue["httpMethod"], groupToValue["pathAndQuery"], groupToValue["cacheStatus"], groupToValue["responseCode"], float32(duration)}, nil
}

const maxLineLength = 8000

func groupToValue(rawLine string) (map[string]string, error) {
  matches := pattern.FindStringSubmatch(rawLine)
  if matches == nil {
    return nil, fmt.Errorf("unknown intbal log line format: %s", rawLine)
  }

  groupToValue := make(map[string]string, numOfGroups)
  for i, group := range groups {
    if i == 0 {
      continue
    }
    groupToValue[group] = matches[i]
  }
  return groupToValue, nil
}

var pattern = regexp.MustCompile(
  "^(?P<timestamp>[0-9]+\\.[0-9]+) " +
  "(?P<responseCode>[0-9]+) " +
  "(?P<cacheStatus>[a-zA-Z-]+) " +
  "\\[(?P<upstreamsDurations>[0-9., -]+)\\] " +
  "\\{(?P<upstreamsIPsAndPorts>[0-9.:, -]+)\\} " +
  "\\{(?P<upstreamsResponsesCodes>[0-9, -]+)\\} " +
  "(?P<callerIP>[0-9.]+) " +
  "(?P<httpMethod>[a-zA-Z]+) " +
  "(?P<pathAndQuery>\\S+) " +
  "(?P<duration>[0-9]+\\.[0-9]+) " +
  // sometimes request id is strangely duplicated
  "\\{(?P<requestId>[0-9a-zA-Z-]+)[,0-9a-z]*\\}")
var groups = pattern.SubexpNames()
var numOfGroups = len(groups)

func (line *line) String() string {
  return fmt.Sprintf("IntbalLogLine{%s %s %s %.3f sec %s %s",
    line.callerIP, line.cacheStatus, line.responseCode, line.durationSec, line.httpMethod, line.pathAndQuery)
}
