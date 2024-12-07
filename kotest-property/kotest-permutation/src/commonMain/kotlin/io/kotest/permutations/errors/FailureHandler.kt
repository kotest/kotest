package io.kotest.permutations.errors

import io.kotest.permutations.IterationFailure
import io.kotest.permutations.PermutationContext

internal object FailureHandler {

   fun handleFailure(context: PermutationContext, result: IterationFailure) {

      val message = PropertyErrorMessageBuilder
         .builder(result.iteration, result.error)
         .withMaxFailures(context.maxFailures)
         .withSeed(context.rs.seed)
         .withInputs(result.inputs)
         .build()

      throw AssertionError(message)
   }

   fun shrinky() {
      // we track any throwables and try to shrink them
//      val shrinks = shrink(context, test)
//      io.kotest.property.internal.throwPropertyTestAssertionError(
//         shrinks,
//         AssertionError(e),
//         index,
//         context.rs.seed
//      )
   }

//   fun buildFailureMessage(
//      context: PropertyContext,
//      inputs: List<Any?>,
//      e: Throwable,
//   ): String {
//      return buildString {
//         appendLine("Property test failed for inputs\n")
//         appendInputs(inputs)
//         appendLine()
//         val cause = stacktraces.root(e)
//         when (val stack = stacktraces.throwableLocation(cause, 4)) {
//            null -> appendLine("Caused by $e")
//            else -> {
//               appendLine("Caused by $e at")
//               stack.forEach { appendLine("\t$it") }
//            }
//         }
//         appendLine()
//      }
//   }

//   private fun buildException(maxFailure: Int, failures: Int, inputs: List<Any?>): String {
//      return buildString {
//         appendLine("Permutation failed $failures times (maxFailure rate was ${maxFailure})")
//         appendLine("Last error was caused by args:")
//         inputs.withIndex().forEach { (index, value) ->
//            appendLine("  $index) ${value.print().value}")
//         }
//      }
//   }
}
