@file:Suppress("DEPRECATION") // All tests in this file cover deprecated functionality

package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.until.PatienceConfig
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.until.fixed
import io.kotest.assertions.until.until
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

@Tags("Deprecated")
class UntilTest : FunSpec({

   test("until with immediate boolean predicate") {
      var attempts = 0
      until(4.seconds) {
         attempts++
         System.currentTimeMillis() > 0
      }
      attempts shouldBe 1
   }

   test("until with boolean predicate that resolves before time duration") {
      var attempts = 0
      until(8.seconds) {
         attempts++
         attempts == 2
      }
      attempts shouldBe 2
   }

   test("until with boolean predicate and interval") {
      var attempts = 0
      until(8.seconds, 10.milliseconds.fixed()) {
         attempts++
         attempts == 10
      }
      attempts shouldBe 10
   }

   test("until with patience config") {
      var attempts = 0
      until(PatienceConfig(4.seconds, 10.milliseconds.fixed())) {
         attempts++
         attempts == 10
      }
      attempts shouldBe 10
   }

   test("until with predicate") {
      var attempts = 0
      var t = ""
      until(8.seconds, { t == "xxx" }) {
         attempts++
         t += "x"
      }
      attempts shouldBe 3
   }

   test("until with predicate and interval") {
      var attempts = 0
      var t = ""
      until(8.seconds, 10.milliseconds.fixed(), { t == "xxxx" }) {
         attempts++
         t += "x"
      }
      attempts shouldBe 4
   }

   test("until should throw when the predicate doesn't equal true in the time period") {
      shouldThrow<AssertionError> {
         until(1.seconds, { it == 2 }) {
            1
         }
      }
   }

   test("until should support fibonacci intervals") {
      var t = ""
      var attempts = 0
      val (result, duration) = measureTimedValue {
         until(10.seconds, 10.milliseconds.fibonacci(), { t == "xxxxxx" }) {
            attempts++
            t += "x"
            t
         }
      }
      attempts shouldBe 6
      result shouldBe "xxxxxx"
      duration shouldBeGreaterThan 100.milliseconds
   }
})
