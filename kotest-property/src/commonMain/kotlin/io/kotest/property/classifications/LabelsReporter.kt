package io.kotest.property.classifications

import io.kotest.common.ExperimentalKotest
import io.kotest.property.PropertyResult
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Pluggable interface for outputting input value labels used in property testing.
 */
@Deprecated("Use StatisticsReporter or update to permutations since 6.0", ReplaceWith("StatisticsReporter"))
@ExperimentalKotest
interface LabelsReporter {
   fun output(result: PropertyResult)
}

@Deprecated("Use StatisticsReporter or update to permutations since 6.0", ReplaceWith("DefaultStatisticsReporter"))
@ExperimentalKotest
object StandardClassificationReporter : LabelsReporter {

   private fun row(label: String, count: Int, attempts: Int, countPad: Int) {
      val percentage = max(((count / attempts.toDouble() * 100.0)).roundToInt(), 1)
      println("${label.padEnd(60, ' ')} ${count.toString().padStart(countPad, ' ')} ($percentage%)")
   }

   override fun output(result: PropertyResult) {
      val countPad = result.attempts.toString().length
      result.inputs.forEach { arg ->
         println("Label statistics for arg $arg (${result.attempts} inputs):")
         result.labels[arg]?.forEach { (label, count) ->
            row(label, count, result.attempts, countPad)
         }
         val other = result.attempts - (result.labels[arg]?.values?.sum() ?: 0)
         if (other > 0) {
            row("OTHER", other, result.attempts, countPad)
         }
         println()
      }
   }
}
