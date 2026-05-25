package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.Classifications
import io.kotest.permutations.PermutationContext
import io.kotest.property.statistics.StatisticsReportMode

/**
 * The [ClassificationsWriter] is responsible for writing the classifications to the output
 * using a [StatisticsReporter].
 */
@OptIn(ExperimentalKotest::class)
internal object ClassificationsWriter {

   suspend fun writeIfEnabled(
      context: PermutationContext,
      success: Boolean,
      attempts: Int,
      classifications: Classifications
   ) {
      when (context.statisticsReportMode) {
         StatisticsReportMode.ON -> doWrite(attempts, classifications)
         StatisticsReportMode.SUCCESS -> if (success) doWrite(attempts, classifications)
         StatisticsReportMode.FAILED -> if (!success) doWrite(attempts, classifications)
         StatisticsReportMode.OFF -> Unit
      }
   }

   private suspend fun doWrite(attempts: Int, classifications: Classifications) {
      DefaultStatisticsReporter.output(attempts, classifications)
   }
}
