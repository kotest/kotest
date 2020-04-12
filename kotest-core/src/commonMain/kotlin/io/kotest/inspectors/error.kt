package io.kotest.inspectors

import io.kotest.assertions.exceptionToMessage
import io.kotest.assertions.failure
import io.kotest.assertions.show.show
import io.kotest.mpp.sysprop

/**
 * Build assertion error message.
 *
 * Show 10 passed and failed results by default. You can change the number of output results by setting the
 * system property `kotlintest.assertions.output.max=20`.
 *
 * E.g.:
 *
 * ```
 *     -Dkotlintest.assertions.output.max=20
 * ```
 */
fun <T> buildAssertionError(msg: String, results: List<ElementResult<T>>): Nothing {

   val maxResults = sysprop("kotlintest.assertions.output.max")?.toInt() ?: 10

   val passed = results.filterIsInstance<ElementPass<T>>()
   val failed = results.filterIsInstance<ElementFail<T>>()

   val builder = StringBuilder(msg)
   builder.append("\n\nThe following elements passed:\n")
   if (passed.isEmpty()) {
      builder.append("--none--")
   } else {
      builder.append(passed.take(maxResults).map { it.t }.joinToString("\n"))
      if (passed.size > maxResults) {
         builder.append("\n... and ${passed.size - maxResults} more passed elements")
      }
   }
   builder.append("\n\nThe following elements failed:\n")
   if (failed.isEmpty()) {
      builder.append("--none--")
   } else {
      builder.append(failed.take(maxResults).joinToString("\n") { it.t.show().value + " => " + exceptionToMessage(it.throwable) })
      if (failed.size > maxResults) {
         builder.append("\n... and ${failed.size - maxResults} more failed elements")
      }
   }
   throw failure(builder.toString())
}

