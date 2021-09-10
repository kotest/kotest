package io.kotest.property

import io.kotest.mpp.atomics.AtomicProperty
import io.kotest.mpp.sysprop
import io.kotest.property.classifications.LabelsReporter
import io.kotest.property.classifications.StandardLabelsReporter
import kotlin.math.max

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
      sysprop("kotest.proptest.default.seed", null, { it.toLong() })
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
}

/**
 * Calculates the default iterations to use for a property test.
 * This value is used when a property test does not specify the iteration count.
 *
 * This is the max of either the [PropertyTesting.defaultIterationCount] or the
 * [calculateMinimumIterations] from the supplied gens.
 */
fun computeDefaultIteration(vararg gens: Gen<*>): Int =
   max(PropertyTesting.defaultIterationCount, calculateMinimumIterations(*gens))

/**
 * Calculates the minimum number of iterations required for the given generators.
 *
 * The value per generator is calcuated as:
 *  - for an [Exhaustive] the total number of values is used
 *  - for an [Arb] the number of edge cases is used
 *
 *  In addition, if all generators are exhaustives, then the cartesian product is used.
 */
fun calculateMinimumIterations(vararg gens: Gen<*>): Int {
   return when {
      gens.all { it is Exhaustive } -> gens.fold(1) { acc, gen -> gen.minIterations() * acc }
      else -> gens.fold(0) { acc, gen -> max(acc, gen.minIterations()) }
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
   val edgeConfig: EdgeConfig = EdgeConfig.default()
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
 * @property iterations The number of iterations to run. If null either the global [PropertyTesting]'s default value
 *                      will be used, or the minimum iterations required for the supplied generations. Whichever is
 *                      greater.
 */
data class PropTestConfig(
   val seed: Long? = PropertyTesting.defaultSeed,
   val minSuccess: Int = PropertyTesting.defaultMinSuccess,
   val maxFailure: Int = PropertyTesting.defaultMaxFailure,
   val shrinkingMode: ShrinkingMode = PropertyTesting.defaultShrinkingMode,
   val iterations: Int? = null,
   val listeners: List<PropTestListener> = PropertyTesting.defaultListeners,
   val edgeConfig: EdgeConfig = EdgeConfig.default(),
   val outputClassifications: Boolean = PropertyTesting.defaultOutputClassifications,
   val labelsReporter: LabelsReporter = StandardLabelsReporter
)

interface PropTestListener {
   suspend fun beforeTest(): Unit = Unit
   suspend fun afterTest(): Unit = Unit
}
