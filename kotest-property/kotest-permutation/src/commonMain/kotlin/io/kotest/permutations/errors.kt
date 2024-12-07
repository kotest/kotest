package io.kotest.permutations

///**
// * Generates an [AssertionError] for a property test without arg details and then throws it.
// */
//internal fun throwPropertyTestAssertionError(
//   e: Throwable, // the underlying failure reason,
//   attempts: Int,
//   seed: Long,
//): Unit = throw propertyAssertionError(e, attempts, seed, emptyList())
//
///**
// * Generates an [AssertionError] for a property test with arg details and then throws it.
// *
// * @param results the reduced (shrunk) values along with the initial values
// * @param e the underlying failure reason
// * @param attempts the iteration count at the time of failure
// */
//fun throwPropertyTestAssertionError(
//   results: List<ShrinkResult<Any?>>,
//   e: Throwable,
//   attempts: Int,
//   seed: Long,
//) {
//   throw propertyAssertionError(e, attempts, seed, results)
//}
//
///**
// * Generates an [AssertionError] for a failed property test.
// *
// * @param e the test failure cause
// * @param attempt the iteration count at the time of failure
// * @param results the inputs that the test failed for
// */
//internal fun propertyAssertionError(
//   e: Throwable,
//   attempt: Int,
//   seed: Long,
//   results: List<ShrinkResult<Any?>>
//): Throwable {
//   return failure(propertyTestFailureMessage(attempt, results, seed, e), e)
//}

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
