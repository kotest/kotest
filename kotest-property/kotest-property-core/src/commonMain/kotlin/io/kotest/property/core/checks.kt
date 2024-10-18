package io.kotest.property.core

import io.kotest.property.MaxDiscardPercentageException
import kotlin.math.min

internal fun checkFailOnSeed(context: PermutationContext, seed: Long?) {
   if (seed != null && context.failOnSeed)
      error("A seed is specified on this property-test and failOnSeed is true")
}

/**
 * Checks that the number of times this permutation passed is at least the
 * configured min success rate.
 */
internal fun checkMinSuccess(context: PermutationContext, result: PermutationResult) {
   val min = min(context.minSuccess, result.evaluations)
   if (result.successes < min) {
      val error = "Property passed ${result.successes} times (minSuccess rate was $min)\n"
      throwPropertyTestAssertionError(AssertionError(error), result.evaluations, result.seed)
   }
}

/**
 * Checks that the number of values discarded by assumptions is less than the configured max discard percentage.
 */
internal fun checkMaxDiscards(context: PermutationContext, result: PermutationResult) {

   if (result.discards > context.maxDiscardPercentage) {
      throw MaxDiscardPercentageException(result.discards, context.maxDiscardPercentage)
   }
}
