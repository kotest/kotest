package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.common.TestNameContextElement
import io.kotest.permutations.Classifications
import io.kotest.permutations.Label
import io.kotest.property.LabelOrder
import io.kotest.property.PropertyTesting
import kotlinx.coroutines.currentCoroutineContext
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Pluggable interface for outputting input value labels used in property testing.
 *
 * This is internal for the first release, we will open this up once we are happy that the API is final.
 */
internal interface StatisticsReporter {
   suspend fun output(iterations: Int, classifications: Classifications)
}

@OptIn(ExperimentalKotest::class)
internal object DefaultStatisticsReporter : StatisticsReporter {

   private fun row(classification: Any?, count: Int, iterations: Int, countPad: Int) {
      val percentage = max(((count / iterations.toDouble() * 100.0)).roundToInt(), 1)
      println("${classification.toString().padEnd(60, ' ')} ${count.toString().padStart(countPad, ' ')} ($percentage%)")
   }

   private suspend fun header(iterations: Int, label: Label): String {
      val testName = currentCoroutineContext()[TestNameContextElement]?.testName
      val prefix = if (testName == null) "" else "[$testName]"
      val suffix = "[${label.value}]"
      return "Statistics: $prefix ($iterations iterations) $suffix"
   }

   private fun stats(stats: Map<Any, Int>, iterations: Int) {
      val countPad = iterations.toString().length
      val sorted = when (PropertyTesting.labelOrder) {
         LabelOrder.Quantity -> stats.toList().sortedByDescending { it.second }
         LabelOrder.Lexicographic -> stats.toList().sortedBy { it.first.toString() }
      }
      sorted.forEach { (classification, count) ->
         row(classification, count, iterations, countPad)
      }
   }

   override suspend fun output(iterations: Int, classifications: Classifications) {
      classifications.counts.forEach { (label, counts) ->
         val header = header(iterations, label)
         println()
         println(header)
         println()
         stats(counts, iterations)
         println()
      }
   }
}
