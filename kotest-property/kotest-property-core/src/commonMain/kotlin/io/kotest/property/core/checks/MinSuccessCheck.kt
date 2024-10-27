package io.kotest.property.core.checks

import io.kotest.property.core.PermutationContext
import io.kotest.property.core.PermutationResult
import io.kotest.property.core.throwPropertyTestAssertionError
import kotlin.math.min

/**
 * Checks that the number of times this permutation passed is at least the
 * configured min success rate.
 */
internal object MinSuccessCheck : AfterCheck {
   override suspend fun evaluate(context: PermutationContext, result: PermutationResult) {
      // if the min success rate is not configured we default to the number of iterations
      val requiredMin = min(context.minSuccess, result.iterations)
      if (result.successes < requiredMin) {
         val error = "Property passed ${result.successes} times (minSuccess rate was $requiredMin)\n"
        throwPropertyTestAssertionError(AssertionError(error), result.iterations, context.rs.seed)
      }
   }
}
