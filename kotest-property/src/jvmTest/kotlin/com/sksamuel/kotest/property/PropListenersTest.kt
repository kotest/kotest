package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
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

   context("listeners bracket every invocation of the property, including shrink runs") {

      var befores = 0
      var afters = 0
      var invocations = 0
      var overlapped = false

      beforeTest {
         befores = 0
         afters = 0
         invocations = 0
         overlapped = false
      }

      val shrinkConfig = PropTestConfig(
         seed = 4815162342,
         edgeConfig = EdgeConfig(edgecasesGenerationProbability = 0.0),
         listeners = listOf(object : PropTestListener {
            override suspend fun beforeTest() {
               ++befores
            }

            override suspend fun afterTest() {
               ++afters
            }
         })
      )

      // records an invocation of the property, and whether exactly one beforeTest was open at the time
      fun invoked() {
         ++invocations
         if (befores - afters != 1) overlapped = true
      }

      test("checkAll with param A") {
         shouldThrowAny {
            checkAll(shrinkConfig, Arb.int(100..1000)) { a ->
               invoked()
               a shouldBeLessThan 100
            }
         }
         invocations shouldBeGreaterThan 1
         overlapped shouldBe false
         befores shouldBe invocations
         afters shouldBe invocations
      }

      test("checkAll with params A, B") {
         shouldThrowAny {
            checkAll(shrinkConfig, Arb.int(100..1000), Arb.int(100..1000)) { a, b ->
               invoked()
               a + b shouldBeLessThan 200
            }
         }
         invocations shouldBeGreaterThan 1
         overlapped shouldBe false
         befores shouldBe invocations
         afters shouldBe invocations
      }

      test("forAll with params A, B, C") {
         shouldThrowAny {
            forAll(shrinkConfig, Arb.int(100..1000), Arb.int(100..1000), Arb.int(100..1000)) { a, b, c ->
               invoked()
               a + b + c < 300
            }
         }
         invocations shouldBeGreaterThan 1
         overlapped shouldBe false
         befores shouldBe invocations
         afters shouldBe invocations
      }

      test("a tolerated failure is still followed by afterTest") {
         var failures = 0
         checkAll(shrinkConfig.copy(iterations = 20, maxFailure = 20, minSuccess = 0), Arb.int(0..1000)) { a ->
            invoked()
            if (a >= 500) ++failures
            a shouldBeLessThan 500
         }
         failures shouldBeGreaterThan 0
         invocations shouldBe 20
         overlapped shouldBe false
         befores shouldBe 20
         afters shouldBe 20
      }
   }
})
