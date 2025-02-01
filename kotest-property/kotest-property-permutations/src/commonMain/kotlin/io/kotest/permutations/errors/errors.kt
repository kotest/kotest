package io.kotest.permutations.errors

import io.kotest.permutations.PermutationContext
import io.kotest.permutations.PermutationResult

internal object ErrorBuilder {

   /**
    * Returns a [AssertionError] which contains the given Throwable [t], decorated with details of
    * the permutation, such as the input args, any shrinks that took place, and the seed used.
    */
   fun decorate(t: Throwable, context: PermutationContext, result: PermutationResult): AssertionError {
      return AssertionError(build(context, result, t)) // todo make sure we always include the original failure
   }

   /**
    * Generates generic permutation failure details, such as the args that failed, any shrinks
    * that took place, and the the seed used.
    */
   fun build(context: PermutationContext, result: PermutationResult, cause: Throwable): String {

      val sb = StringBuilder()
      sb.append("Property failed after ${result.attempts} iterations\n")
      if (result.shrinks.isNotEmpty()) {
         sb.append("\n")
//         result.shrinks.withIndex().forEach { (index, result) ->
//            val input = if (result.initial == result.shrink) {
//               "\tArg ${index}: ${result.initial.print().value}"
//            } else {
//               "\tArg ${index}: ${result.shrink.print().value} (shrunk from ${result.initial})"
//            }
//            sb.append(input)
//            sb.append("\n")
//         }
      }
      sb.append("\n")
      sb.append("Repeat this test by using seed ${context.rs.seed}\n\n")

      // the cause we use in the final result is the result of the last shrinking step, otherwise we use the original
      val finalCause = cause // todo result.shrinks.fold(cause) { t, result -> result.cause ?: t }

      // don't bother to include the exception type if it's AssertionError
      val causedBy = when (finalCause::class.simpleName) {
         "AssertionError" -> "Caused by: ${finalCause.message?.trim()}"
         else -> "Caused by ${finalCause::class.simpleName}: ${finalCause.message?.trim()}"
      }

      sb.append(causedBy)
      return sb.toString()
   }
}
