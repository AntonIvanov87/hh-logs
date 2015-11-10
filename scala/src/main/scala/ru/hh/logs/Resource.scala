package ru.hh.logs

case class Resource(method: String, path: String) {
  override def toString: String = s"$method $path"
}
