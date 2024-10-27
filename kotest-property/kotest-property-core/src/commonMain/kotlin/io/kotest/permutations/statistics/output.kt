package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyTesting

@ExperimentalKotest
suspend fun outputStatistics(context: PropertyContext, args: Int, success: Boolean) {
   suspend fun write() {
      val statistics = Statistics(
         context.attempts(),
         args,
         context.labels(),
         context.statistics(),
         success = success,
      )
      PropertyTesting.statisticsReporter.output(statistics)
   }
   when (PropertyTesting.statisticsReportMode) {
      StatisticsReportMode.ON -> write()
      StatisticsReportMode.SUCCESS -> if (success) write()
      StatisticsReportMode.FAILED -> if (!success) write()
      StatisticsReportMode.OFF -> Unit
   }
}
