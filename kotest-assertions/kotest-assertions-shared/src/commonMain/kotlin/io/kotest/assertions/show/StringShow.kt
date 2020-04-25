package io.kotest.assertions.show

object StringShow : Show<String> {

   private fun String.wrap() = """"$this""""

   fun showNoWrap(a: String): Printed = when {
      a == "" -> "<empty string>".printed()
      a.isBlank() -> a.replace(" ", "\\s").wrap().printed()
      else -> a.printed()
   }

   override fun show(a: String): Printed = when {
      a == "" -> "<empty string>".printed()
      a.isBlank() -> a.replace(" ", "\\s").wrap().printed()
      else -> a.wrap().printed()
   }
}
