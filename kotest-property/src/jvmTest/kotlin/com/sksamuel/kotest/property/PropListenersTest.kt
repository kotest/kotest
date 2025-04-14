package com.sksamuel.kotest.property

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.*
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

@EnabledIf(LinuxOnlyGithubCondition::class)
class PropListenersTest : FunSpec({
   var previous = -1
   var current = 0
   var total = 0

   beforeTest {
      previous = -1
      current = 0
      total = 0
   }

   val propConfig = PropTestConfig(
      listeners = listOf(object : PropTestListener {
         override suspend fun beforeTest() {
            previous = current
            ++current
         }

         override suspend fun afterTest() {
            ++total
         }
      })
   )

   test("checkAll calls listener for param A") {
      checkAll(10, propConfig, Arb.string()) {
         current shouldBe (previous + 1)
         total shouldBe previous
      }
      previous shouldBe 9
      current shouldBe 10
      total shouldBe 10
   }

   test("checkAll calls listener for params A, B") {
      checkAll(
         10,
         propConfig,
         Arb.string(),
         Arb.int()
      ) { _, _ ->
         current shouldBe (previous + 1)
         total shouldBe previous
      }
      previous shouldBe 9
      current shouldBe 10
      total shouldBe 10
   }

   test("checkAll calls listener for params A, B, C") {
      checkAll(
         10,
         propConfig,
         Arb.string(),
         Arb.string(),
         Arb.int()
      ) { _, _, _ ->
         current shouldBe (previous + 1)
         total shouldBe previous
      }
      previous shouldBe 9
      current shouldBe 10
      total shouldBe 10
   }

   test("checkAll calls listener for params A, B, C, D") {
      checkAll(
         10,
         propConfig,
         Arb.string(),
         Arb.int(),
         Arb.string(),
         Arb.int()
      ) { _, _, _, _ ->
         current shouldBe (previous + 1)
         total shouldBe previous
      }
      previous shouldBe 9
      current shouldBe 10
      total shouldBe 10
   }

   test("checkAll calls listener for params A, B, C, D, E") {
      checkAll(
         10,
         propConfig,
         Arb.string(),
         Arb.int(),
         Arb.string(),
         Arb.string(),
         Arb.int()
      ) { _, _, _, _, _ ->
         current shouldBe (previous + 1)
         total shouldBe previous
      }
      previous shouldBe 9
      current shouldBe 10
      total shouldBe 10
   }

   test("checkAll calls listener for params A, B, C, D, E, F") {
      checkAll(
         10,
         propConfig,
         Arb.string(),
         Arb.int(),
         Arb.string(),
         Arb.int(),
         Arb.string(),
         Arb.int()
      ) { _, _, _, _, _, _ ->
         current shouldBe (previous + 1)
         total shouldBe previous
      }
      previous shouldBe 9
      current shouldBe 10
      total shouldBe 10
   }
})
