@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.permutations.permutations
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.statistics.Label
import io.kotest.property.statistics.StatisticsReportMode

@OptIn(ExperimentalKotest::class)
class StatisticsReporterTest : FunSpec() {
   init {

      test("the statistics reporter should receive classifications recorded under the default label") {
         val captor = CapturingStatisticsReporter()
         permutations {
            iterations = 10
            statisticsReporter = captor
            statisticsReportMode = StatisticsReportMode.ON
            val a by gen { Arb.int(0..100) }
            check {
               classify(a % 2 == 0, "even", "odd")
            }
         }

         captor.received shouldHaveSize 1
         val counts = captor.received.single().counts[Label.Default] ?: error("missing default-label counts")
         counts.keys shouldBe setOf("even", "odd")
         counts.values.sum() shouldBe 10
      }

      test("the statistics reporter should receive classifications keyed by custom label") {
         val captor = CapturingStatisticsReporter()
         permutations {
            iterations = 6
            statisticsReporter = captor
            statisticsReportMode = StatisticsReportMode.ON
            val a by gen { Arb.int(-50..50) }
            check {
               classify("parity", a % 2 == 0, "even", "odd")
               classify("sign", a >= 0, "non-negative", "negative")
            }
         }

         captor.received shouldHaveSize 1
         val classifications = captor.received.single()
         classifications.counts.keys shouldBe setOf(Label("parity"), Label("sign"))
         classifications.counts.getValue(Label("parity")).values.sum() shouldBe 6
         classifications.counts.getValue(Label("sign")).values.sum() shouldBe 6
      }

      test("the statistics reporter should not be invoked when the report mode is OFF") {
         val captor = CapturingStatisticsReporter()
         permutations {
            iterations = 5
            statisticsReporter = captor
            statisticsReportMode = StatisticsReportMode.OFF
            val a by gen { Arb.int(0..10) }
            check {
               classify(a % 2 == 0, "even", "odd")
            }
         }

         captor.received shouldHaveSize 0
      }
   }

   private class CapturingStatisticsReporter : StatisticsReporter {
      val received = mutableListOf<Classifications>()
      override suspend fun output(classifications: Classifications) {
         received += classifications
      }
   }
}
