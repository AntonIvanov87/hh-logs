package intbal
import (
  "strings"
  "regexp"
)

type resource struct {
  method string
  path string
}

func normalize(pathAndQuery string) string {

  result := strings.Split(pathAndQuery, "?")[0]
  result = slashesRegexp.ReplaceAllString(result, "/")
  result = idsRegexp.ReplaceAllString(result, "/id(s)$1")
  return result
}

var slashesRegexp = regexp.MustCompile("/{2,}")
var idsRegexp = regexp.MustCompile("/[0-9A-F,-]+(/|$)")
