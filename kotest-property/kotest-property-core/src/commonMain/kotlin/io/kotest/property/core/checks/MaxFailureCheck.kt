package io.kotest.property.core.checks

import io.kotest.property.core.IterationResult
import io.kotest.property.core.PermutationContext

/**
 * An [IterationCheck] that will check if the number of failures has exceeded the max failure rate.
 */
//internal object MaxFailureCheck : IterationCheck {
//
//   override fun evaluate(context: PermutationContext, result: IterationResult) {
//      if (result.failures > context.maxFailures) {
//         error(buildException(context.maxFailures, result.failures, result.inputs))
//      }
//   }
//
//   private fun buildException(maxFailure: Int, failures: Int, inputs: List<Any?>): String {
//      return buildString {
//         appendLine("Permutation failed ${failures} times (maxFailure rate was ${maxFailure})")
//         appendLine("Last error was caused by args:")
//         inputs.withIndex().forEach { (index, value) ->
//            appendLine("  $index) ${value.print().value}")
//         }
//      }
//   }
//}

internal object FailureHandler {
   fun handleFailure(context: PermutationContext, result: IterationResult) {
      if (context.maxFailures == 0) {
//         throw AssertionError(buildException(context.maxFailures, result.failures, result.inputs))
      } else if (result.failures > context.maxFailures) {
//         throw AssertionError(buildException(context.maxFailures, result.failures, result.inputs))
      }

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
}
