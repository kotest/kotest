package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.internal.proptest

class PropTestConfigTest : FunSpec() {
   init {
      test("PropTestConfig iterations should be used by proptest1 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            PropTestConfig(iterations = expectedIterations)
         ) {
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest2 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations)
         ) { _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest3 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest4 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest5 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest6 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest7 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest8 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest9 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            Arb.int(),
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest10 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
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
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest11 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
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
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }

      test("PropTestConfig iterations should be used by proptest12 if present") {
         val expectedIterations = 10

         var iterationCount = 0
         proptest(
            1000,
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
            PropTestConfig(iterations = expectedIterations))
         { _, _, _, _, _, _, _, _, _, _, _, _ ->
            iterationCount++
         }

         iterationCount shouldBe expectedIterations
      }
   }
}
