package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Constraints
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.internal.proptest
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class PropTestConfigConstraintsTest : FunSpec() {
   init {
      test("PropTestConfig constraints should be used by proptest1 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            ) {
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest2 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            ) { _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest3 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest4 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest5 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest6 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest7 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest8 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest9 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest10 if present") {
         checkAll(Arb.int(1..10)) { iterations ->


            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest11 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest12 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest13 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest14 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest15 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest16 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest17 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest18 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest19 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest20 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest21 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should be used by proptest22 if present") {
         checkAll(Arb.int(1..10)) { iterations ->
            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            )
            { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe iterations
         }
      }

      test("PropTestConfig constraints should support durations") {
         val config = PropTestConfig(constraints = Constraints.duration(200.milliseconds))
         val start = TimeSource.Monotonic.markNow()
         checkAll(config, Arb.string()) { _ -> }
         // we should have exited around 200 millis
         start.elapsedNow().inWholeMilliseconds.shouldBeGreaterThan(150)
         start.elapsedNow().inWholeMilliseconds.shouldBeLessThan(300)
      }
   }
}
