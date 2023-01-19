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

   override fun print(a: String): Printed = print(a, 0)

   override fun print(a: String, level: Int): Printed = when {
      a == "" -> "<empty string>".printed()
      a.isBlank() -> "<blank string>".printed()
      // Skip wrapping top-level strings with quotes. It makes easier copying the values to the code
      level == 0 -> a.printed()
      else -> a.wrap().printed()
   }
}
