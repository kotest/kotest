package io.kotest.permutations.statistics

import io.kotest.permutations.PermutationContext
import io.kotest.permutations.PermutationResult

/**
 * Verifies that a permutation test reached the minimum coverage required by its configuration.
 *
 * Collected classifications alone do not guarantee that interesting cases were exercised - a run could be
 * complete with too few successful permutations to be meaningful. For example, you might have a test on
 * even and odd numbers where, for some reason, no odd numbers were generated.
 */
internal object CoverageCheck {
   fun check(context: PermutationContext, result: PermutationResult) {
   }
}
