package io.kotest.property.core

import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.core.constraints.Constraints
import io.kotest.property.core.constraints.Iteration
import io.kotest.property.core.delegates.GenDelegateRegistry

/**
 * The immutable state of a property test.
 */
internal data class PermutationContext(
   val constraints: Constraints,
   val maxDiscardPercentage: Int,
   val shrinkingMode: ShrinkingMode,
   val shouldPrintShrinkSteps: Boolean,
   val shouldPrintGeneratedValues: Boolean,
   val outputClassifications: Boolean,
   val shouldPrintConfig: Boolean,
   val failOnSeed: Boolean,
   val edgecasesGenerationProbability: Double,
   val minSuccess: Int,
   val maxFailures: Int,
   val random: RandomSource,
   val customSeed: Boolean, // true if the seed was set programmatically
   val registry: GenDelegateRegistry,
   val statistics: Statistics,
   val beforePermutation: suspend () -> Unit,
   val afterPermutation: suspend () -> Unit,
   val writeFailedSeed: Boolean,
   val test: suspend Iteration.() -> Unit,
)

