package io.kotest.permutations.statistics

import io.kotest.common.TestNameContextElement
import io.kotest.property.LabelOrder
import io.kotest.property.PropertyTesting
import io.kotest.property.statistics.Label
import kotlin.coroutines.coroutineContext
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Pluggable interface for outputting input value labels used in property testing.
 */
interface StatisticsReporter {
   suspend fun output(classifications: Classifications)
}

object DefaultStatisticsReporter : StatisticsReporter {

   private fun row(classification: Any?, count: Int, iterations: Int, countPad: Int) {
      val percentage = max(((count / iterations.toDouble() * 100.0)).roundToInt(), 1)
      println("${classification.toString().padEnd(60, ' ')} ${count.toString().padStart(countPad, ' ')} ($percentage%)")
   }

   private suspend fun header(iterations: Int, args: Int, label: Label?): String {
      val testName = coroutineContext[TestNameContextElement]?.testName
      val prefix = if (testName == null) "" else "[$testName]"
      val suffix = if (label == null) "" else "[${label.value}]"
      return "Statistics: $prefix ($iterations iterations, $args args) $suffix"
   }

   private fun stats(stats: Map<Any?, Int>, iterations: Int) {
      val countPad = iterations.toString().length
      val sorted = when (PropertyTesting.labelOrder) {
         LabelOrder.Quantity -> stats.toList().sortedByDescending { it.second }
         LabelOrder.Lexicographic -> stats.toList().sortedBy { it.first.toString() }
      }
      sorted.forEach { (classification, count) ->
         row(classification, count, iterations, countPad)
      }
   }

   override suspend fun output(classifications: Classifications) {
//      val unlabelled = statistics.statistics[null]
//      if (unlabelled != null && unlabelled.isNotEmpty()) {
//         val header = header(statistics.iterations, statistics.args, null)
//         println()
//         println(header)
//         println()
//         stats(unlabelled, statistics.iterations)
//         println()
//      }
//
//      statistics.statistics.forEach { (label, stats) ->
//         if (label != null) {
//            val header = header(statistics.iterations, statistics.args, label)
//            println()
//            println(header)
//            println()
//            stats(stats, statistics.iterations)
//            println()
//         }
//      }
   }
}
