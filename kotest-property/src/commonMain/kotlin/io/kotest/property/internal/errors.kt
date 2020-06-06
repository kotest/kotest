package io.kotest.property.internal

import io.kotest.assertions.failure
import io.kotest.assertions.show.show

/**
 * Generates an [AssertionError] for a property test without arg details and then throws it.
 */
internal fun throwPropertyTestAssertionError(
   e: Throwable, // the underlying failure reason,
   attempts: Int,
   seed: Long
): Unit = throw propertyAssertionError(e, attempts, seed, emptyList())

/**
 * Generates an [AssertionError] for a property test with arg details and then throws it.
 *
 * @param results the reduced (shrunk) values along with the initial values
 * @param e the underlying failure reason
 * @param attempts the iteration count at the time of failure
 */
internal fun throwPropertyTestAssertionError(
   results: List<ShrinkResult<Any?>>,
   e: Throwable,
   attempts: Int,
   seed: Long
) {
   throw propertyAssertionError(e, attempts, seed, results)
}

/**
 * Generates an [AssertionError] for a failed property test.
 *
 * @param e the test failure cause
 * @param attempt the iteration count at the time of failure
 * @param results the inputs that the test failed for
 */
internal fun propertyAssertionError(
   e: Throwable,
   attempt: Int,
   seed: Long,
   results: List<ShrinkResult<Any?>>
): Throwable {
   return failure(propertyTestFailureMessage(attempt, results, seed, e), e)
}

/**
 * Generates a property test failure message with details of the args that failed, any shrinks
 * that took place, and the exception throw by the failing test.
 */
internal fun propertyTestFailureMessage(
   attempt: Int,
   results: List<ShrinkResult<Any?>>,
   seed: Long,
   cause: Throwable
): String {
   val sb = StringBuilder()
   sb.append("Property failed after $attempt attempts\n")
   if (results.isNotEmpty()) {
      sb.append("\n")
      results.withIndex().forEach { (index, result) ->
         val input = if (result.initial == result.shrink) {
            "\tArg ${index}: ${result.initial.show().value}"
         } else {
            "\tArg ${index}: ${result.shrink.show().value} (shrunk from ${result.initial})"
         }
         sb.append(input)
         sb.append("\n")
      }
   }
   sb.append("\n")
   sb.append("Repeat this test by using seed $seed\n\n")

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
