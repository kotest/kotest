@file:Suppress("DEPRECATION") // Remove when removing legacy until

package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@Tags("Deprecated")
class UntilSpec : FunSpec({
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
      until({
         duration = 8.seconds.inWholeMicroseconds
         interval = 10L.fixed()
      }) {
         ++attempts == 100
      }

      attempts shouldBe 100
   }

   test("until with predicate") {
      var attempts = 0
      var t = ""
      until(8.seconds) {
         attempts++
         t += "x"
         t == "xxx"
      }
      attempts shouldBe 3
   }

   test("until with predicate and interval") {
      var attempts = 0
      var t = ""
      until({
         duration = 8.seconds.inWholeMilliseconds
         interval = 10L.fixed()
      }) {
         attempts++
         t += "x"
         t == "xxxx"
      }

      attempts shouldBe 4
   }

   test("until should throw when the predicate doesn't equal true in the time period") {
      shouldThrow<AssertionError> {
         until(1.seconds) {
            false
         }
      }
   }

   test("until should support fibonacci intervals") {
      measureTime {
         var t = ""
         var attempts = 0
         until({
            duration = 10.seconds.inWholeMilliseconds
            interval = 10L.fibonacci()
         }) {
            attempts++
            t += "x"
            t == "xxxxxx"
         }
         attempts shouldBe 6
         t shouldBe "xxxxxx"
      }.shouldBeGreaterThan(100.milliseconds)
   }
})
