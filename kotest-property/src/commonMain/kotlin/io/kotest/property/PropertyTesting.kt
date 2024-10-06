package io.kotest.property

import io.kotest.mpp.sysprop
import io.kotest.property.statistics.DefaultStatisticsReporter
import io.kotest.property.statistics.StatisticsReportMode
import io.kotest.property.statistics.StatisticsReporter
import kotlin.random.Random

/**
 * Global object containing settings for property testing.
 */
object PropertyTesting {

   var maxFilterAttempts: Int = 10

   var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", true)

   var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", false)

   var shouldPrintConfig: Boolean = sysprop("kotest.proptest.output.config", false)

   var defaultSeed: Long = sysprop("kotest.proptest.default.seed", Random.nextLong()) { it.toLong() }

   var defaultMinSuccess: Int = sysprop("kotest.proptest.default.min-success", Int.MAX_VALUE)

   var defaultMaxFailure: Int = sysprop("kotest.proptest.default.max-failure", 0)

   var defaultIterationCount: Int = sysprop("kotest.proptest.default.iteration.count", 1000)

   var defaultShrinkingMode: ShrinkingMode = when(val mode =
     sysprop("kotest.proptest.default.shrinking.mode", "bounded")) {
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

   var maxDiscardPercentage: Int = sysprop("kotest.proptest.max.discard.percentage", 20)

   var labelOrder: LabelOrder = LabelOrder.Quantity

   var statisticsReporter: StatisticsReporter = DefaultStatisticsReporter

   var statisticsReportMode: StatisticsReportMode = StatisticsReportMode.ON
}
