package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.permutations.Classifications
import io.kotest.permutations.Label

@OptIn(ExperimentalKotest::class)
class StatisticsReporterTest : FunSpec() {
   init {

      test("DefaultStatisticsReporter should print a section header for each label") {
         val classifications = Classifications(
            mutableMapOf(
               Label("parity") to mutableMapOf<Any, Int>("even" to 5, "odd" to 5),
               Label("sign") to mutableMapOf<Any, Int>("positive" to 7, "negative" to 3),
            )
         )

         val stdout = captureStandardOut {
            DefaultStatisticsReporter.output(10, classifications)
         }

         stdout shouldContain "Statistics:"
         stdout shouldContain "(10 iterations)"
         stdout shouldContain "[parity]"
         stdout shouldContain "[sign]"
      }

      test("DefaultStatisticsReporter should print each classification with its count and percentage") {
         val classifications = Classifications(
            mutableMapOf(
               Label("parity") to mutableMapOf<Any, Int>("even" to 3, "odd" to 7),
            )
         )

         val stdout = captureStandardOut {
            DefaultStatisticsReporter.output(10, classifications)
         }

         stdout shouldContain "even"
         stdout shouldContain "odd"
         stdout shouldContain "3"
         stdout shouldContain "7"
         stdout shouldContain "(30%)"
         stdout shouldContain "(70%)"
      }

      test("DefaultStatisticsReporter should use the default label header when no custom label was set") {
         val classifications = Classifications(
            mutableMapOf(
               Label.Default to mutableMapOf<Any, Int>("yes" to 4, "no" to 6),
            )
         )

         val stdout = captureStandardOut {
            DefaultStatisticsReporter.output(10, classifications)
         }

         stdout shouldContain "[${Label.Default.value}]"
         stdout shouldContain "yes"
         stdout shouldContain "no"
      }

      test("DefaultStatisticsReporter should round small percentages up to at least 1%") {
         val classifications = Classifications(
            mutableMapOf(
               Label.Default to mutableMapOf<Any, Int>("rare" to 1),
            )
         )

         val stdout = captureStandardOut {
            DefaultStatisticsReporter.output(1000, classifications)
         }

         stdout shouldContain "rare"
         stdout shouldContain "(1%)"
      }

      test("DefaultStatisticsReporter should produce no output for an empty Classifications") {
         val stdout = captureStandardOut {
            DefaultStatisticsReporter.output(5, Classifications())
         }

         stdout shouldNotContain "Statistics:"
      }
   }
}
