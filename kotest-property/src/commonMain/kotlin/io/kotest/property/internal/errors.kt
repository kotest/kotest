package io.kotest.property.internal

import io.kotest.assertions.print.print

/**
 * Generates an [AssertionError] for a property test without arg details and then throws it.
 */
internal fun throwPropertyTestAssertionError(
   e: Throwable, // the underlying failure reason,
   attempts: Int,
   seed: Long,
   evalIndex: Int? = null,
): Unit = throw propertyAssertionError(e, attempts, seed, emptyList(), false, evalIndex)

/**
 * Generates an [AssertionError] for a property test with arg details and then throws it.
 *
 * @param results the reduced (shrunk) values along with the initial values
 * @param e the underlying failure reason
 * @param attempts the iteration count at the time of failure
 * @param evalIndex the pre-assumption iteration index ([io.kotest.property.PropertyContext.evals])
 *                  at the time of failure. Suitable for [io.kotest.property.PropTestConfig.skipTo]
 *                  to jump back to this iteration on a rerun.
 */
@JvmOverloads
fun throwPropertyTestAssertionError(
   results: List<ShrinkResult<Any?>>,
   e: Throwable,
   attempts: Int,
   seed: Long,
   outputHexForUnprintableChars: Boolean,
   evalIndex: Int? = null,
) {
   throw propertyAssertionError(e, attempts, seed, results, outputHexForUnprintableChars, evalIndex)
}

/**
 * Generates an [AssertionError] for a failed property test.
 *
 * If the failure cause carries expected/actual diff values (e.g. from a matcher backed by OpenTest4J
 * on the JVM, or [io.kotest.assertions.KotestAssertionFailedError] on other platforms), those values
 * are propagated to the resulting error so that IDEs such as IntelliJ display a "click to see diff" link.
 *
 * @param e the test failure cause
 * @param attempt the iteration count at the time of failure
 * @param results the inputs that the test failed for
 */
internal fun propertyAssertionError(
   e: Throwable,
   attempt: Int,
   seed: Long,
   results: List<ShrinkResult<Any?>>,
   outputHexForUnprintableChars: Boolean,
   evalIndex: Int? = null,
): Throwable {
   val finalCause = results.fold(e) { t, result -> result.cause ?: t }
   val message = propertyTestFailureMessage(attempt, results, seed, e, outputHexForUnprintableChars, evalIndex)
   return createPropertyAssertionError(message, finalCause)
}

/**
 * Creates an [AssertionError] for a property test failure, preserving any expected/actual
 * values present on [cause] so that IDEs can render a diff for the outer error.
 */
internal expect fun createPropertyAssertionError(message: String, cause: Throwable): AssertionError

/**
 * Generates a property test failure message with details of the args that failed, any shrinks
 * that took place, and the exception throw by the failing test.
 */
internal fun propertyTestFailureMessage(
   attempt: Int,
   results: List<ShrinkResult<Any?>>,
   seed: Long,
   cause: Throwable,
   outputHexForUnprintableChars: Boolean,
   evalIndex: Int? = null,
): String {
   val sb = StringBuilder()
   sb.append("Property failed after $attempt attempts\n")
   if (results.isNotEmpty()) {
      sb.append("\n")
      results.withIndex().forEach { (index, result) ->
         val initialPrintable = result.initial.print().value.let {
            if (outputHexForUnprintableChars) it.escapeUnprintable() else it
         }
         val shrinkPrintable = result.shrink.print().value.let {
            if (outputHexForUnprintableChars) it.escapeUnprintable() else it
         }

         val input: String = if (result.initial == result.shrink) {
            "\tArg ${index}: $initialPrintable"
         } else {
            "\tArg ${index}: $shrinkPrintable (shrunk from $initialPrintable)"
         }
         sb.append(input)
         sb.append("\n")
      }
   }
   sb.append("\n")
   sb.append("Repeat this test by using seed $seed\n")
   if (evalIndex != null) {
      // Eval index is the pre-assumption iteration counter (PropertyContext.evals) at failure
      // time. Drop it into PropTestConfig.skipTo on a rerun to jump back to this exact iteration.
      sb.append("Eval index: $evalIndex\n")
   }
   // Per-arg shrink paths: positions in [RTree.children] traversed at each level. Printed only
   // when at least one arg actually shrunk so non-shrinking cases (Exhaustive, ShrinkingMode.Off)
   // stay quiet. Pair with the Eval index above and PropTestConfig.shrinkPaths to replay this
   // failure directly without re-running the shrink search.
   val shrinkPaths = results.map { it.path }
   if (shrinkPaths.any { it.isNotEmpty() }) {
      sb.append("Shrink paths: $shrinkPaths\n")
   }
   sb.append("\n")

   // the cause we use in the final result is the result of the last shrinking step, otherwise we use the original
   val finalCause = results.fold(cause) { t, result -> result.cause ?: t }

   // don't bother to include the exception type if it's AssertionError
   val causedBy = when (finalCause::class.simpleName) {
      "AssertionError" -> "Caused by: ${finalCause.message?.trim()}"
      else -> "Caused by ${finalCause::class.simpleName}: ${finalCause.message?.trim()}"
   }
   sb.append(causedBy)
   return sb.toString()
}
