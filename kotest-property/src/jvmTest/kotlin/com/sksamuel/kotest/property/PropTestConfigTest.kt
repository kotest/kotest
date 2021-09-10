package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import io.kotest.property.internal.proptest

class PropTestConfigTest : FunSpec() {
   init {
      test("PropTestConfig iterations should be used by proptest1 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

            var iterationCount = 0
            proptest(
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            ) {
               iterationCount++
            }

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest2 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

            var iterationCount = 0
            proptest(
               Arb.int(),
               Arb.int(),
               PropTestConfig(constraints = Constraints.iterations(iterations))
            ) { _, _ ->
               iterationCount++
            }

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest3 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest4 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest5 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest6 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest7 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest8 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest9 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest10 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest11 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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

            iterationCount shouldBe expectedIterations
         }
      }

      test("PropTestConfig iterations should be used by proptest12 if present") {
         checkAll(Arb.int(1..1000).orNull()) { iterations ->
            val expectedIterations = iterations ?: PropertyTesting.defaultIterationCount

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
               PropTestConfig(constraints = Constraints.iterations(iterations)))
            { _, _, _, _, _, _, _, _, _, _, _, _ ->
               iterationCount++
            }

            iterationCount shouldBe expectedIterations
         }
      }
   }
}
