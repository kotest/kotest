package com.sksamuel.kotest.property

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

@EnabledIf(LinuxOnlyGithubCondition::class)
class CheckAllArity2 : FunSpec() {
   init {

      test("checkAll/defaultIterations/specifiedArbs") {

         val context = checkAll(
            Arb.int(),
            Arb.int()
         ) { a, b ->
            a + b shouldBe b + a
         }

         context.attempts() shouldBe 1000
         context.successes() shouldBe 1000
         context.failures() shouldBe 0
      }

      test("checkAll/customIterations/specifiedArbs") {

         val context = checkAll(
            19,
            Arb.int(),
            Arb.int()
         ) { a, b ->
            a + b shouldBe b + a
         }

         context.attempts() shouldBe 19
         context.successes() shouldBe 19
         context.failures() shouldBe 0
      }

      test("checkAll/defaultIterations/inferredArbs") {

         val context = checkAll<Int, Int> { a, b ->
            a + b shouldBe b + a
         }

         context.attempts() shouldBe 1000
         context.successes() shouldBe 1000
         context.failures() shouldBe 0
      }

      test("checkAll/customIterations/inferredArbs") {

         val context = checkAll<Int, Int>(55) { a, b ->
            a + b shouldBe b + a
         }

         context.attempts() shouldBe 55
         context.successes() shouldBe 55
         context.failures() shouldBe 0
      }

      test("checkAll/customConfig/specifiedArbs") {

         val context = checkAll(
            config = PropTestConfig(seed = 333, iterations = 35),
            Arb.int(),
            Arb.int()
         ) { a, b ->
            a + b shouldBe b + a
         }

         context.attempts() shouldBe 35
         context.successes() shouldBe 35
         context.failures() shouldBe 0
      }

   }
}
