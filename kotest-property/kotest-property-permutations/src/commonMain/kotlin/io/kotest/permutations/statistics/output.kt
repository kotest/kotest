package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.PermutationContext
import io.kotest.property.statistics.StatisticsReportMode

/**
 * The [ClassificationsWriter] is responsible for writing the classifications to the output.
 */
@ExperimentalKotest
internal object ClassificationsWriter {

   suspend fun writeIfEnabled(context: PermutationContext, success: Boolean) {
      when (context.statisticsReportMode) {
         StatisticsReportMode.ON -> doWrite(context)
         StatisticsReportMode.SUCCESS -> if (success) doWrite(context)
         StatisticsReportMode.FAILED -> if (!success) doWrite(context)
         StatisticsReportMode.OFF -> Unit
      }
   }

   private suspend fun doWrite(context: PermutationContext) {
      val classifications = Classifications()
//         context.attempts(),
//         args,
//         context.labels(),
//         context.statistics(),
//         success = success,
//      )
      context.statisticsReporter.output(classifications)
   }
}
