package io.kotest.assertions.show

object StringShow : Show<String> {
   override fun show(a: String): String = when {
      a == "" -> "<empty string>"
      a.isBlank() -> a.replace(" ", "\\s")
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
