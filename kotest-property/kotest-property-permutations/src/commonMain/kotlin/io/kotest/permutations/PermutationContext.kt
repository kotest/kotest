@file:OptIn(ExperimentalKotest::class)

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.constraints.Constraints
import io.kotest.permutations.delegates.GenDelegateRegistry
import io.kotest.permutations.statistics.CoverageConfiguration
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.statistics.StatisticsReportMode

/**
 * The immutable state of a permutation test.
 *
 * This class combines the configuration supplied by a user from a [PermutationConfiguration] object,
 * along with defaults for anything not specified, and includes additional information required
 * by the permutation executor - such as the random source, and classifications tracking.
 */
data class PermutationContext(
   val constraints: Constraints,
   val maxDiscardPercentage: Int,
   val discardCheckThreshold: Int,
   val shrinkingMode: ShrinkingMode,
   val printShrinkSteps: Boolean,
   val printGeneratedValues: Boolean,
   val printConfig: Boolean,
   val failOnSeed: Boolean,
   val writeFailedSeed: Boolean,
   val customSeed: Boolean, // true if the seed was set programmatically
   val rs: RandomSource, // the final random source, either from custom seed or random seed
   val minSuccess: Int,
   val maxFailures: Int,
   val outputStatistics: Boolean,
   val classifications: Classifications,
   val coverage: CoverageConfiguration,
   val statisticsReportMode: StatisticsReportMode,
   val registry: GenDelegateRegistry,
   val beforePermutation: suspend () -> Unit,
   val afterPermutation: suspend () -> Unit,
   val test: suspend Permutation.() -> Unit,
)

