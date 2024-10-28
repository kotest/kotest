package io.kotest.permutations.checks

import io.kotest.assertions.print.print
import io.kotest.mpp.stacktraces
import io.kotest.permutations.IterationResult
import io.kotest.permutations.PermutationContext
import io.kotest.property.PropertyContext

internal object FailureHandler {

   fun handleFailure(context: PermutationContext, result: IterationResult) {
//         throw AssertionError(ErrorBuilder.build(context.maxFailures, result.failures, result.inputs))
//         throw AssertionError(buildException(context.maxFailures, result.failures, result.inputs))

      //      if (config.maxFailure == 0) {
//         printFailureMessage(context, inputs, e)
//         io.kotest.property.internal.throwPropertyTestAssertionError(shrinkfn(), e, context.attempts(), seed)
//      } else if (context.failures() > config.maxFailure) {
//         io.kotest.property.internal.throwPropertyTestAssertionError(
//            shrinkfn(),
//            AssertionError(error),
//            context.attempts(),
//            seed
//         )
//      }
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

   fun buildFailureMessage(
      context: PropertyContext,
      inputs: List<Any?>,
      e: Throwable,
   ): String {
      return buildString {
         appendLine("Property test failed for inputs\n")
         appendInputs(inputs)
         appendLine()
         val cause = stacktraces.root(e)
         when (val stack = stacktraces.throwableLocation(cause, 4)) {
            null -> appendLine("Caused by $e")
            else -> {
               appendLine("Caused by $e at")
               stack.forEach { appendLine("\t$it") }
            }
         }
         appendLine()
      }
   }

   private fun StringBuilder.appendInputs(inputs: List<Any?>) {
      iterator {
         inputs.forEach { input ->
            yield(input.print().value)
         }
//         context.generatedSamples().forEach { sample ->
//            yield("${sample.value.print().value} (generated within property context)")
//         }
      }.withIndex().forEach { (index, input) ->
         appendLine("$index) $input")
      }
   }

   private fun buildException(maxFailure: Int, failures: Int, inputs: List<Any?>): String {
      return buildString {
         appendLine("Permutation failed $failures times (maxFailure rate was ${maxFailure})")
         appendLine("Last error was caused by args:")
         inputs.withIndex().forEach { (index, value) ->
            appendLine("  $index) ${value.print().value}")
         }
      }
   }
}
