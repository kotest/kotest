package io.kotlintest.assertions.show

object StringShow : Show<String> {
  override fun show(a: String): String = when (a) {
    "" -> "<empty string>"
    else -> a
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\'", "\\\'")
      .replace("\t", "\\\t")
      .replace("\b", "\\\b")
      .replace("\n", "\\\n")
      .replace("\r", "\\\r")
      .replace("\$", "\\\$")
  }
}
