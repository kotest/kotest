package io.kotest.assertions.print

/**
 * An instance of [Print] for strings that will quote the string,
 * use <empty string> in place for "", and escape whitespace for blank strings.
 */
object StringPrint : Print<String> {

   private fun String.wrap() = """"$this""""

   fun showNoWrap(a: String): Printed = when {
      a == "" -> "<empty string>".printed()
      a.isBlank() -> a.replace(" ", "\\s").wrap().printed()
      else -> a.printed()
   }

   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: String): Printed = when {
      a == "" -> "<empty string>".printed()
      a.isBlank() -> a.replace(" ", "\\s").wrap().printed()
      else -> a.wrap().printed()
   }
}
