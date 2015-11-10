package ru.hh.logs

private [logs] class TooLongLineException(line: String) extends RuntimeException("too long line: " + line.substring(0, 50) + "...")
