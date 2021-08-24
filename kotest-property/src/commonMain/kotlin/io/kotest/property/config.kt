package io.kotest.property

import io.kotest.mpp.sysprop
import io.kotest.property.classifications.LabelsReporter
import io.kotest.property.classifications.StandardLabelsReporter
import kotlin.math.max
import kotlin.native.concurrent.ThreadLocal

/**
 * Global object for containing settings for property testing.
 */
@ThreadLocal
object PropertyTesting {
   var maxFilterAttempts: Int = 10

   // PropTestConfig
   var defaultSeed: Long? = null
   var defaultMinSuccess: Int = Int.MAX_VALUE
   var defaultMaxFailure: Int = 0
   var defaultShrinkingMode: ShrinkingMode = ShrinkingMode.Bounded(1000)
   var defaultIterationCount: Int = sysprop("kotest.proptest.default.iteration.count", "1000").toInt()
   var defaultListeners: List<PropTestListener> = listOf()
   var defaultEdgecasesGenerationProbability: Double = sysprop("kotest.proptest.arb.edgecases-generation-probability", "0.02").toDouble()
   @Deprecated("Use defaultEdgecasesGenerationProbability instead. This property will be removed")
   var edgecasesGenerationProbability: Double
      get() = defaultEdgecasesGenerationProbability
      set(value) { defaultEdgecasesGenerationProbability = value }
   var defaultOutputClassifications: Boolean = sysprop("kotest.proptest.arb.output.classifications", "false") == "true"

   var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", "true") == "true"
   @Deprecated("This property is no longer used and will be removed")
   var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", "false") == "true"
   @Deprecated("This property is no longer used and will be removed")
   var edgecasesBindDeterminism: Double = sysprop("kotest.proptest.arb.edgecases-bind-determinism", "0.9").toDouble()

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
