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
   var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", "false") == "true"
   var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", "true") == "true"
   var defaultIterationCount: Int = sysprop("kotest.proptest.default.iteration.count", "1000").toInt()
   var edgecasesGenerationProbability: Double = sysprop("kotest.proptest.arb.edgecases-generation-probability", "0.02").toDouble()
   var edgecasesBindDeterminism: Double = sysprop("kotest.proptest.arb.edgecases-bind-determinism", "0.9").toDouble()
   var outputClassifiations: Boolean = sysprop("kotest.proptest.arb.output.classificaions", "false") == "true"
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
   edgecasesGenerationProbability = PropertyTesting.edgecasesGenerationProbability
)

data class PropTest(
   val seed: Long? = null,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinkingMode: ShrinkingMode = ShrinkingMode.Bounded(1000),
   val iterations: Int? = null,
   val listeners: List<PropTestListener> = listOf(),
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

data class PropTestConfig(
   val seed: Long? = null,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinkingMode: ShrinkingMode = ShrinkingMode.Bounded(1000),
   val iterations: Int? = null,
   val listeners: List<PropTestListener> = listOf(),
   val edgeConfig: EdgeConfig = EdgeConfig.default(),
   val outputLabels: Boolean? = null,
   val labelsReporter: LabelsReporter = StandardLabelsReporter
)

interface PropTestListener {
   suspend fun beforeTest(): Unit = Unit
   suspend fun afterTest(): Unit = Unit
}
