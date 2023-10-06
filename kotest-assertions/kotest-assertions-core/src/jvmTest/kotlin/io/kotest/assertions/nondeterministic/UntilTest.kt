package io.kotest.assertions.nondeterministic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class UntilTest : FunSpec({

   test("until with immediate pass") {
      var attempts = 0
      until(1.seconds) {
         attempts++
         System.currentTimeMillis() > 0
      }
      attempts shouldBe 1
   }

   test("until should exit as soon as predicate passes") {
      until(1.days) {
         System.currentTimeMillis() > 0
      }
   }

   test("until with boolean predicate that resolves before time duration") {
      var attempts = 0
      until(3.seconds) {
         attempts++
         attempts == 10
      }
      attempts shouldBe 10
   }

   test("until with config") {
      var attempts = 0
      val config = untilConfig {
         duration = 2.seconds
         interval = 10.milliseconds
      }
      until(config) {
         attempts++
         attempts == 100
      }
      attempts shouldBe 100
   }

   test("until with listener") {
      val latch = CountDownLatch(5)
      val config = untilConfig {
         duration = 2.seconds
         interval = 10.milliseconds
         listener = { _, _ -> latch.countDown() }
      }
      var t = ""
      until(config) {
         t += "x"
         t == "xxxxxx"
      }
      latch.await(15, TimeUnit.SECONDS) shouldBe true
   }
})
