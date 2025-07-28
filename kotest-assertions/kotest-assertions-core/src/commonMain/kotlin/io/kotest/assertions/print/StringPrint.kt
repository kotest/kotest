package io.kotest.assertions.print

/**
 * An instance of [Print] for strings that will quote the string,
 * use <empty string> in place for "", and escape whitespace for blank strings.
 */
object StringPrint : Print<String> {

   private fun String.wrap() = """"$this""""

   override fun print(a: String, level: Int): Printed = when {
      a == "" -> Printed("<empty string>", String::class)
      a.isBlank() -> Printed(a.replace(" ", "\\s").wrap(), String::class)
      else -> Printed(a.wrap(), String::class)
   }
}
