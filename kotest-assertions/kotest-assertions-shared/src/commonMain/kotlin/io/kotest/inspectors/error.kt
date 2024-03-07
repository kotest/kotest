package io.kotest.inspectors

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.exceptionToMessage
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

/**
 * Build assertion error message.
 *
 * Show 10 passed and failed results by default. You can change the number of output results by setting the
 * system property `kotest.assertions.output.max=20`.
 *
 * E.g.:
 *
 * ```
 *     -Dkotest.assertions.output.max=20
 * ```
 */
@PublishedApi
internal fun <T> buildAssertionError(msg: String, results: List<ElementResult<T>>): Nothing {

   val maxResults = AssertionsConfig.maxErrorsOutput

   val passed = results.filterIsInstance<ElementPass<T>>()
   val failed = results.filterIsInstance<ElementFail<T>>()

   throw failure(
      buildString {
         appendLine(msg)


         // Print passed elements
         appendLine()
         appendLine("The following elements passed:")

         if (passed.isEmpty()) {
            appendLine("  --none--")
         } else {
            passed.take(maxResults)
               .forEach {
                  appendLine("  [${it.index}] ${it.t}") // Why not print().value??
               }

            if (passed.size > maxResults) {
               appendLine("  ... and ${passed.size - maxResults} more passed elements")
            }
         }

         // Print failed elements
         appendLine()
         appendLine("The following elements failed:")

         if (failed.isEmpty()) {
            // Can this really happen, given that we're in the buildAssertionError function?
            appendLine("  --none--")
         } else {
            failed.take(maxResults)
               .forEach {
                  appendLine("  [${it.index}] ${it.t.print().value} => ${exceptionToMessage(it.throwable)}")
               }

            if (failed.size > maxResults) {
               appendLine("  ... and ${failed.size - maxResults} more failed elements")
            }
         }
      }
   )
}

