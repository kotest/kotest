package io.kotest.property

import io.kotest.common.ExperimentalKotest
import io.kotest.property.classifications.ClassificationReporter
import io.kotest.property.classifications.StandardClassificationReporter

enum class LabelOrder {
   Quantity,
   Lexicographic,
}

fun EdgeConfig.Companion.default(): EdgeConfig = EdgeConfig(
   edgecasesGenerationProbability = PropertyTesting.defaultEdgecasesGenerationProbability
)

data class PropTest(
   val seed: Long? = PropertyTesting.defaultSeed,
   val minSuccess: Int = PropertyTesting.defaultMinSuccess,
   val maxFailure: Int = PropertyTesting.defaultMaxFailure,
   val shrinkingMode: ShrinkingMode = PropertyTesting.defaultShrinkingMode,
   val iterations: Int? = null,
   val listeners: List<PropTestListener> = PropertyTesting.defaultListeners,
   val edgeConfig: EdgeConfig = EdgeConfig.default(),
   val constraints: Constraints? = null,
)

fun PropTest.toPropTestConfig() =
   PropTestConfig(
      seed = seed,
      minSuccess = minSuccess,
      maxFailure = maxFailure,
      iterations = iterations,
      shrinkingMode = shrinkingMode,
      listeners = listeners,
      edgeConfig = edgeConfig
   )

/**
 * Property Test Configuration to be used by the underlying property test runner
 *
 * @param iterations The number of iterations to run. If null either the global [PropertyTesting]'s default value
 *                      will be used, or the minimum iterations required for the supplied generations. Whichever is
 *                      greater.
 *
 * @param constraints controls the loop for properties. See [Constraints].
 */
@OptIn(ExperimentalKotest::class)
data class PropTestConfig(
   val seed: Long? = PropertyTesting.defaultSeed,
   val minSuccess: Int = PropertyTesting.defaultMinSuccess,
   val maxFailure: Int = PropertyTesting.defaultMaxFailure,
   val shrinkingMode: ShrinkingMode = PropertyTesting.defaultShrinkingMode,
   val iterations: Int? = null,
   val listeners: List<PropTestListener> = PropertyTesting.defaultListeners,
   val edgeConfig: EdgeConfig = EdgeConfig.default(),
   val outputClassifications: Boolean = PropertyTesting.defaultOutputClassifications,
   val classificationReporter: ClassificationReporter = StandardClassificationReporter,
   val constraints: Constraints? = null,
   val maxDiscardPercentage: Int = 20,
   val skipTo: Int = 0
)

interface PropTestListener {
   suspend fun beforeTest(): Unit = Unit
   suspend fun afterTest(): Unit = Unit
}
