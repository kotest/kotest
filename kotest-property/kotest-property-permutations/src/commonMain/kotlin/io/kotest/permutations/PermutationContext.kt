package io.kotest.permutations

import io.kotest.permutations.constraints.Constraints
import io.kotest.permutations.delegates.GenDelegateRegistry
import io.kotest.permutations.statistics.StatisticsReporter
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.statistics.StatisticsReportMode

/**
 * The immutable state of a permutation test.
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
   val edgecasesGenerationProbability: Double,
   val minSuccess: Int,
   val maxFailures: Int,
   val outputStatistics: Boolean,
   val statisticsReporter: StatisticsReporter,
   val statisticsReportMode: StatisticsReportMode,
   val requiredCoveragePercentages: Map<Any?, Double>,
   val requiredCoverageCounts: Map<Any?, Int>,
   val registry: GenDelegateRegistry,
   val beforePermutation: suspend () -> Unit,
   val afterPermutation: suspend () -> Unit,
   val test: suspend Permutation.() -> Unit,
)

