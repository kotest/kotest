package io.kotest.permutations.checks

import io.kotest.permutations.PermutationContext
import kotlin.math.roundToInt

internal object MaxDiscardCheck {

   fun check(context: PermutationContext, discards: Int, iterations: Int) {
      val discardPercentage = discardPercentage(discards, iterations)
      if (discardPercentage > context.maxDiscardPercentage) {
         error("Percentage of discarded inputs ($discardPercentage%) exceeds max (${context.maxDiscardPercentage}%). Adjust your generators to increase the probability of an acceptable value, or increase the max discard percentage in permutation config.")
      }
   }

   /**
    * Returns an Int that is the rounded percentage of discarded inputs (failed assumptions) from all inputs.
    */
   internal fun discardPercentage(discards: Int, iterations: Int): Int {
      return if (discards == 0 || iterations == 0)
         0
      else
         (discards / iterations.toDouble() * 100.0).roundToInt()
   }
}
