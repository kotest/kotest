package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.common.sysprop
import io.kotest.property.ShrinkingMode
import io.kotest.property.statistics.StatisticsReportMode

/**
 * Global object containing settings for permutation testing.
 */
@ExperimentalKotest
object PermutationTesting {

   var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", true)

   var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", false)

   var shouldPrintConfig: Boolean = sysprop("kotest.proptest.output.config", false)

   /**
    * The maximum percentage of discards allowed before the test aborts to avoid infinite loops.
    */
   var maxDiscardPercentage: Int = sysprop("kotest.proptest.max.discard.percentage", 20)

   /**
    * The threshold at which we start checking for the max discard percentage.
    * Otherwise, we would fail on the first discards as it would be 100% of the iterations.
    */
   var discardCheckThreshold: Int = sysprop("kotest.proptest.discard.threshold", 50)

   var defaultSeed: Long? = sysprop("kotest.proptest.default.seed", null) { it.toLong() }

   var defaultMinSuccess: Int = sysprop("kotest.proptest.default.min-success", Int.MAX_VALUE)

   var defaultMaxFailure: Int = sysprop("kotest.proptest.default.max-failure", 0)

   var defaultIterationCount: Int = sysprop("kotest.proptest.default.iteration.count", 1000)

   var defaultShrinkingMode: ShrinkingMode =
      when (val mode = sysprop("kotest.proptest.default.shrinking.mode", "bounded")) {
         "off" -> ShrinkingMode.Off
         "bounded" -> ShrinkingMode.Bounded(sysprop("kotest.proptest.default.shrinking.bound", 1000))
         "unbounded" -> ShrinkingMode.Unbounded
         else -> error("Invalid shrinking mode: $mode")
      }

   var defaultOutputClassifications: Boolean = sysprop("kotest.proptest.arb.output.classifications", false)

   var failOnSeed: Boolean = sysprop("kotest.proptest.seed.fail-if-set", false)

   var writeFailedSeed: Boolean = sysprop("kotest.proptest.seed.write-failed", true)

   var labelOrder: LabelOrder = LabelOrder.Quantity

   var statisticsReportMode: StatisticsReportMode = StatisticsReportMode.ON

   var defaultOutputHexForUnprintableChars: Boolean =
      sysprop("kotest.proptest.arb.string.output-hex-for-unprintable-chars", false)

   var defaultCollectionsRange: IntRange = 0..100
}

/**
 * Controls the order in which classifications are sorted when written by the statistics reporter.
 */
enum class LabelOrder {
   Quantity,
   Lexicographic,
}
