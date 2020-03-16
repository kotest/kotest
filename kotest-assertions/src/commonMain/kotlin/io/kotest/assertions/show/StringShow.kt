package io.kotest.assertions.show

object StringShow : Show<String> {

   private fun String.wrap() = """"$this""""

   override fun show(a: String): Printed = when {
      a == "" -> "<empty string>".printed()
      a.isBlank() -> a.replace(" ", "\\s").wrap().printed()
      else -> a
         .replace("\\", "\\\\")
         .replace("\'", "\\\'")
         .replace("\t", "\\\t")
         .replace("\"", "\\\"")
         .replace("\b", "\\\b")
         .replace("\r", "\\\r")
         .replace("\$", "\\\$")
         .wrap()
         .printed()
   }
}
