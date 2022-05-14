package io.kotest.property

import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.atomics.AtomicProperty
import io.kotest.mpp.sysprop
import io.kotest.property.classifications.LabelsReporter
import io.kotest.property.classifications.StandardLabelsReporter

/**
 * Global object containing settings for property testing.
 */
object PropertyTesting {
   var maxFilterAttempts: Int by AtomicProperty {
      10
   }
   var shouldPrintShrinkSteps: Boolean by AtomicProperty {
      sysprop("kotest.proptest.output.shrink-steps", true)
   }
   var shouldPrintGeneratedValues: Boolean by AtomicProperty {
      sysprop("kotest.proptest.output.generated-values", false)
   }
   var edgecasesBindDeterminism: Double by AtomicProperty {
      sysprop("kotest.proptest.arb.edgecases-bind-determinism", 0.9)
   }
   var defaultSeed: Long? by AtomicProperty {
      sysprop("kotest.proptest.default.seed", null) { it.toLong() }
   }
   var defaultMinSuccess: Int by AtomicProperty {
      sysprop("kotest.proptest.default.min-success", Int.MAX_VALUE)
   }
   var defaultMaxFailure: Int by AtomicProperty {
      sysprop("kotest.proptest.default.max-failure", 0)
   }
   var defaultIterationCount: Int by AtomicProperty {
      sysprop("kotest.proptest.default.iteration.count", 1000)
   }
   var defaultShrinkingMode: ShrinkingMode by AtomicProperty {
      ShrinkingMode.Bounded(1000)
   }
   var defaultListeners: List<PropTestListener> by AtomicProperty {
      listOf()
   }
   var defaultEdgecasesGenerationProbability: Double by AtomicProperty {
      sysprop("kotest.proptest.arb.edgecases-generation-probability", 0.02)
   }
   var defaultOutputClassifications: Boolean by AtomicProperty {
      sysprop("kotest.proptest.arb.output.classifications", false)
   }
   var failOnSeed: Boolean by AtomicProperty {
      sysprop("kotest.proptest.seed.fail-if-set", false)
   }
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
   val labelsReporter: LabelsReporter = StandardLabelsReporter,
   val constraints: Constraints? = null,
   val maxDiscardPercentage: Int = 10,
)

interface PropTestListener {
   suspend fun beforeTest(): Unit = Unit
   suspend fun afterTest(): Unit = Unit
}
