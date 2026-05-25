package io.kotest.permutations.statistics

import io.kotest.permutations.PermutationContext
import io.kotest.permutations.PermutationResult
import io.kotest.permutations.errors.ErrorBuilder

/**
 * Verifies that a permutation test reached the minimum coverage required by its configuration.
 *
 * Collected classifications alone do not guarantee that interesting cases were exercised - a run could be
 * complete with too few successful permutations to be meaningful. For example, you might have a test on
 * even and odd numbers where, for some reason, no odd numbers were generated.
 */
internal object CoverageCheck {
   fun check(context: PermutationContext, result: PermutationResult) {
      val counts = context.classifications.counts

      context.coverage.coverageCounts.forEach { (label, value, required) ->
         val actual = counts[label]?.get(value) ?: 0
         if (actual < required) {
            val message = "Required coverage count for [$value] under label [${label.value}] " +
               "was $required but actual was $actual\n"
            throw ErrorBuilder.decorate(AssertionError(message), context, result)
         }
      }

      context.coverage.coveragePercentages.forEach { (label, value, requiredPercent) ->
         val actual = counts[label]?.get(value) ?: 0
         val actualPercent = if (result.attempts == 0) 0.0
         else (actual.toDouble() / result.attempts.toDouble()) * 100.0
         if (actualPercent < requiredPercent) {
            val message = "Required coverage percentage for [$value] under label [${label.value}] " +
               "was $requiredPercent% but actual was $actualPercent%\n"
            throw ErrorBuilder.decorate(AssertionError(message), context, result)
         }
      }
   }
}
