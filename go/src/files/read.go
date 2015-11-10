package files

import (
  "os"
  "bufio"
  "io"
)

// Reads file and writes its' lines into the returned channel.
// Assumes that a line can fit bufio.defaultBufSize, otherwise truncates line.
func Read(fileName string) (<-chan string, error) {
  file, err := os.Open(fileName)
  if err != nil {
    return nil, err
  }
  lines := make(chan string, 10)
  go func() {
    readToChan(file, lines)
    file.Close()
  }()
  return lines, nil
}

func readToChan(reader io.Reader, lines chan<- string) {
  bufReader := bufio.NewReader(reader)
  // TODO: remove i
  for i := 0; i < 100; i++ {
    line, err := bufReader.ReadString('\n')
    if err == io.EOF  {
      break
    }
    if err != nil {
      break
      // XXX: how to signal err?
    }
    lines <- line
  }
  close(lines)
}
