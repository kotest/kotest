package io.kotest.permutations.checks

import io.kotest.permutations.PermutationContext
import kotlin.math.roundToInt

internal object MaxDiscardCheck {

   fun check(context: PermutationContext, discards: Int, iterations: Int) {
      if (discards < context.discardCheckThreshold) return
      val discardPercentage = discardPercentage(discards, iterations)
      if (discardPercentage > context.maxDiscardPercentage) {
         error(errorMessage(discards, iterations, context.maxDiscardPercentage, discardPercentage))
      }
   }

   internal fun errorMessage(
      discards: Int,
      iterations: Int,
      maxDiscardPercentage: Int,
      discardPercentage: Int
   ): String {
      return buildString {
         appendLine("Percentage of discarded inputs ($discards/$iterations $discardPercentage%) exceeds max ($maxDiscardPercentage%).")
         appendLine("Adjust your generators to increase the probability of an acceptable value, or increase the max discard percentage in permutation config.")
      }
   }

   /**
    * Returns an Int that is the rounded percentage of discarded inputs (failed assumptions) from all inputs.
    */
   internal fun discardPercentage(discards: Int, iterations: Int): Int {
      return when {
         discards == 0 && iterations == 0 -> 0
         iterations == 0 -> 100
         else -> (discards / iterations.toDouble() * 100.0).roundToInt()
      }
   }

   fun ensureConfigured(maxDiscardPercentage: Int) {
      if (maxDiscardPercentage == 0) {
         error("Max discard percentage must be configured to use assumptions")
      }
   }
}
