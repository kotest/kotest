package io.kotest.property.core

import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.classifications.ClassificationReporter
import io.kotest.property.core.constraints.Constraints
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
   val shouldPrintConfig: Boolean,
   val failOnSeed: Boolean,
   val writeFailedSeed: Boolean,
   val customSeed: Boolean, // true if the seed was set programmatically
   val rs: RandomSource,
   val edgecasesGenerationProbability: Double,
   val minSuccess: Int,
   val maxFailures: Int,
   val outputClassifications: Boolean,
   val classificationReporter: ClassificationReporter,
   val registry: GenDelegateRegistry,
   val beforePermutation: suspend () -> Unit,
   val afterPermutation: suspend () -> Unit,
   val test: suspend Permutation.() -> Unit,
)

