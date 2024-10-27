package io.kotest.property.core.checks

import io.kotest.property.core.PermutationContext
import io.kotest.property.core.PermutationResult
import kotlin.math.roundToInt

internal object MaxDiscardCheck : AfterCheck {

   override suspend fun evaluate(context: PermutationContext, result: PermutationResult) {
      val discardPercentage = discardPercentage(result)
      if (discardPercentage > context.maxDiscardPercentage) {
         error("Percentage of discarded inputs ($discardPercentage%) exceeds max (${context.maxDiscardPercentage}%). Adjust your generators to increase the probability of an acceptable value, or increase the max discard percentage in permutation config.")
      }
   }

   /**
    * Returns an Int that is the rounded percentage of discarded inputs (failed assumptions) from all inputs.
    */
   internal fun discardPercentage(result: PermutationResult): Int {
      return if (result.discards == 0 || result.iterations == 0)
         0
      else
         (result.discards / result.iterations.toDouble() * 100.0).roundToInt()
   }
}
