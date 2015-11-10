package main

import (
  "os"
  "log"
  "files"
  "logs/intbal"
)

func main() {
  fileName := fileNameFromArgs()

  lines, err := files.Read(fileName)
  if err != nil {
    log.Fatal(err)
  }

  intbal.ProcessLines(lines)
}

func fileNameFromArgs() string {
  if len(os.Args) <= 1 {
    log.Fatalf("usage: %s file", os.Args[0])
  }
  return os.Args[1]
}

