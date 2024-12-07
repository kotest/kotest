package io.kotest.property

import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.sysprop
import io.kotest.property.classifications.LabelsReporter
import io.kotest.property.classifications.StandardClassificationReporter
import io.kotest.property.statistics.DefaultStatisticsReporter
import io.kotest.property.statistics.StatisticsReportMode
import io.kotest.property.statistics.StatisticsReporter

/**
 * Global object containing settings for property testing.
 */
object PropertyTesting {

   var maxFilterAttempts: Int = 10

   var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", true)

   var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", false)

   var shouldPrintConfig: Boolean = sysprop("kotest.proptest.output.config", false)

   var edgecasesBindDeterminism: Double = sysprop("kotest.proptest.arb.edgecases-bind-determinism", 0.9)

   /**
    * The maximum percentage of discards allowed before the test aborts to avoid infinite loops.
    */
   var maxDiscardPercentage: Int = sysprop("kotest.proptest.max.discard.percentage", 20)

   /**
    * The threshold at which we start checking for the max discard percentage.
    * Otherwise we would fail on the first discards as it would be 100% of the iterations.
    */
   var discardCheckThreshold: Int = sysprop("kotest.proptest.discard.threshold", 50)

   var defaultSeed: Long? = sysprop("kotest.proptest.default.seed", null) { it.toLong() }

   var defaultMinSuccess: Int = sysprop("kotest.proptest.default.min-success", Int.MAX_VALUE)

   var defaultMaxFailure: Int = sysprop("kotest.proptest.default.max-failure", 0)

   var defaultIterationCount: Int = sysprop("kotest.proptest.default.iteration.count", 1000)

   var defaultShrinkingMode: ShrinkingMode = when(val mode = sysprop("kotest.proptest.default.shrinking.mode", "bounded")) {
      "off" -> ShrinkingMode.Off
      "bounded" -> ShrinkingMode.Bounded(sysprop("kotest.proptest.default.shrinking.bound", 1000))
      "unbounded" -> ShrinkingMode.Unbounded
      else -> error("Invalid shrinking mode: $mode")
   }

   var defaultListeners: List<PropTestListener> = listOf()

   var defaultEdgecasesGenerationProbability: Double =
      sysprop("kotest.proptest.arb.edgecases-generation-probability", 0.02)

   var defaultOutputClassifications: Boolean = sysprop("kotest.proptest.arb.output.classifications", false)

   var failOnSeed: Boolean = sysprop("kotest.proptest.seed.fail-if-set", false)

   var writeFailedSeed: Boolean = sysprop("kotest.proptest.seed.write-failed", true)

   var labelOrder: LabelOrder = LabelOrder.Quantity

   @ExperimentalKotest
   var statisticsReporter: StatisticsReporter = DefaultStatisticsReporter

   @ExperimentalKotest
   var statisticsReportMode: StatisticsReportMode = StatisticsReportMode.ON

   var defaultOutputHexForUnprintableChars: Boolean =
      sysprop("kotest.proptest.arb.string.output-hex-for-unprintable-chars", false)
}

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
   val outputHexForUnprintableChars: Boolean = PropertyTesting.defaultOutputHexForUnprintableChars
)

fun PropTest.toPropTestConfig() =
   PropTestConfig(
      seed = seed,
      minSuccess = minSuccess,
      maxFailure = maxFailure,
      iterations = iterations,
      shrinkingMode = shrinkingMode,
      listeners = listeners,
      edgeConfig = edgeConfig,
      outputHexForUnprintableChars = outputHexForUnprintableChars,
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
   val classificationReporter: LabelsReporter = StandardClassificationReporter,
   val constraints: Constraints? = null,
   val maxDiscardPercentage: Int = 20,
   val skipTo: Int = 0,
   val outputHexForUnprintableChars: Boolean = PropertyTesting.defaultOutputHexForUnprintableChars,
)

interface PropTestListener {
   suspend fun beforeTest(): Unit = Unit
   suspend fun afterTest(): Unit = Unit
}
