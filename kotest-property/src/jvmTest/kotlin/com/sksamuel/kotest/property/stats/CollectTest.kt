package com.sksamuel.kotest.property.stats

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.LabelOrder
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import io.kotest.property.statistics.StatisticsReportMode
import io.kotest.property.statistics.withCoverageCount
import io.kotest.property.statistics.withCoverageCounts
import io.kotest.property.statistics.withCoveragePercentage
import io.kotest.property.statistics.withCoveragePercentages
import java.math.RoundingMode

@EnabledIf(LinuxOnlyGithubCondition::class)
class CollectTest : FunSpec() {
   init {
      test("collecting stats") {
         val stdout = captureStandardOut {
            checkAll(PropTestConfig(seed = 1231245), Arb.enum<RoundingMode>()) {
               collect(it)
            }
         }
         stdout.shouldContain("Statistics: [collecting stats] (1000 iterations, 1 args)")
         stdout.lines().joinToString(separator = "\n").shouldContain(
            """
HALF_DOWN                                                     142 (14%)
HALF_UP                                                       141 (14%)
CEILING                                                       132 (13%)
FLOOR                                                         122 (12%)
UP                                                            119 (12%)
UNNECESSARY                                                   119 (12%)
HALF_EVEN                                                     118 (12%)
DOWN                                                          107 (11%)
""".trim()
         )
      }

      test("collecting labelled stats") {
         val stdout = captureStandardOut {
            checkAll(PropTestConfig(seed = 1231245), Arb.enum<RoundingMode>()) {
               collect(it)
               collect("mylabel", it)
               collect("mylabel2", it)
            }
         }
         stdout.shouldContain("Statistics: [collecting labelled stats] (1000 iterations, 1 args)")
         stdout.shouldContain("Statistics: [collecting labelled stats] (1000 iterations, 1 args) [mylabel]")
         stdout.shouldContain("Statistics: [collecting labelled stats] (1000 iterations, 1 args) [mylabel2]")
         stdout.lines().joinToString(separator = "\n").shouldContain(
            """
HALF_DOWN                                                     142 (14%)
HALF_UP                                                       141 (14%)
CEILING                                                       132 (13%)
FLOOR                                                         122 (12%)
UP                                                            119 (12%)
UNNECESSARY                                                   119 (12%)
HALF_EVEN                                                     118 (12%)
DOWN                                                          107 (11%)
""".trim()
         )
      }

      test("collecting labelled stats order by label name") {

         PropertyTesting.labelOrder = LabelOrder.Lexicographic

         val stdout = captureStandardOut {
            checkAll(PropTestConfig(seed = 1231245), Arb.enum<RoundingMode>()) {
               collect(it)
               collect("mylabel", it)
               collect("mylabel2", it)
            }
         }
         stdout.shouldContain("Statistics: [collecting labelled stats order by label name] (1000 iterations, 1 args)")
         stdout.shouldContain("Statistics: [collecting labelled stats order by label name] (1000 iterations, 1 args) [mylabel]")
         stdout.shouldContain("Statistics: [collecting labelled stats order by label name] (1000 iterations, 1 args) [mylabel2]")
         stdout.lines().joinToString(separator = "\n").shouldContain(
            """
CEILING                                                       132 (13%)
DOWN                                                          107 (11%)
FLOOR                                                         122 (12%)
HALF_DOWN                                                     142 (14%)
HALF_EVEN                                                     118 (12%)
HALF_UP                                                       141 (14%)
UNNECESSARY                                                   119 (12%)
UP                                                            119 (12%)
""".trim()
         )

         PropertyTesting.labelOrder = LabelOrder.Quantity
      }

      test("no output when StatisticsReportMode.OFF") {
         PropertyTesting.statisticsReportMode = StatisticsReportMode.OFF
         val stdout = captureStandardOut {
            checkAll(Arb.enum<RoundingMode>()) {
               collect(it)
               collect("mylabel", it)
               collect("mylabel2", it)
            }
         }
         stdout.shouldNotContain("mylabel")
         stdout.shouldNotContain("mylabel2")
         stdout.shouldNotContain("Statistics:")
         stdout.shouldNotContain("1000 iterations")
      }

      test("no output for successful test and StatisticsReportMode.FAILED") {
         PropertyTesting.statisticsReportMode = StatisticsReportMode.FAILED
         val stdout = captureStandardOut {
            checkAll(Arb.enum<RoundingMode>()) {
               collect(it)
               collect("mylabel", it)
               collect("mylabel2", it)
            }
         }
         stdout.shouldNotContain("mylabel")
         stdout.shouldNotContain("mylabel2")
         stdout.shouldNotContain("Statistics:")
         stdout.shouldNotContain("1000 iterations")
      }

      test("no output for failed test and StatisticsReportMode.SUCCESS") {
         PropertyTesting.statisticsReportMode = StatisticsReportMode.SUCCESS
         val stdout = captureStandardOut {
            shouldThrowAny {
               checkAll(Arb.enum<RoundingMode>()) {
                  it shouldBe RoundingMode.HALF_UP
                  collect(it)
                  collect("mylabel", it)
                  collect("mylabel2", it)
               }
            }
         }
         stdout.shouldNotContain("mylabel")
         stdout.shouldNotContain("mylabel2")
         stdout.shouldNotContain("Statistics:")
         stdout.shouldNotContain("1000 iterations")
      }

      test("withCoveragePercentage should fail if coverage % is not sufficient") {
         shouldThrowAny {
            withCoveragePercentage(RoundingMode.HALF_UP, 30.0) {
               checkAll(PropTestConfig(seed = 1), Arb.enum<RoundingMode>()) {
                  collect(it)
               }
            }
         }.message.shouldContain("Required coverage of 30.0% for [HALF_UP] but was [12%]")
      }

      test("withCoveragePercentages should fail if coverage % is not sufficient") {
         shouldThrowAny {
            withCoveragePercentages(mapOf(RoundingMode.HALF_UP to 2.0, RoundingMode.FLOOR to 30.0)) {
               checkAll(PropTestConfig(seed = 1), Arb.enum<RoundingMode>()) {
                  collect(it)
               }
            }
         }.message.shouldContain("Required coverage of 30.0% for [FLOOR] but was [13%]")
      }

      test("withCoverageCount should fail if coverage number is not sufficient") {
         shouldThrowAny {
            withCoverageCount(RoundingMode.HALF_UP, 200) {
               checkAll(PropTestConfig(seed = 1), Arb.enum<RoundingMode>()) {
                  collect(it)
               }
            }
         }.message.shouldContain("Required coverage of 200 for [HALF_UP] but was [125]")
      }

      test("withCoverageCounts should fail if coverage number is not sufficient") {
         shouldThrowAny {
            withCoverageCounts(mapOf(RoundingMode.HALF_UP to 44, RoundingMode.FLOOR to 200)) {
               checkAll(PropTestConfig(seed = 1), Arb.enum<RoundingMode>()) {
                  collect(it)
               }
            }
         }.message.shouldContain("Required coverage of 200 for [FLOOR] but was [137]")
      }

      test("withCoverageCounts should pass for sufficient coverage number") {
         withCoverageCounts(mapOf(RoundingMode.HALF_UP to 75, RoundingMode.FLOOR to 75)) {
            checkAll(PropTestConfig(seed = 1), Arb.enum<RoundingMode>()) {
               collect(it)
            }
         }
      }
   }
}
