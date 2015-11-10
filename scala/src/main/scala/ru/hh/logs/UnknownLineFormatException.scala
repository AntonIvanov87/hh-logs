package ru.hh.logs

private[logs] class UnknownLineFormatException(line: String) extends RuntimeException("unknown line format: " + line)
