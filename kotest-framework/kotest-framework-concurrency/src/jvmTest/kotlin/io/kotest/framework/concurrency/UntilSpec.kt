package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@ExperimentalKotest
class UntilSpec : FunSpec({
   test("until with immediate boolean predicate") {
      var attempts = 0
      until(1.seconds) {
         attempts++
         System.currentTimeMillis() > 0
      }
      attempts shouldBe 1
   }

   test("until with boolean predicate that resolves before time duration") {
      var attempts = 0
      until(3.seconds) {
         attempts++
         attempts == 2
      }
      attempts shouldBe 2
   }

   test("until with boolean predicate and interval") {
      var attempts = 0
      until({
         duration = 2.seconds.inWholeMicroseconds
         interval = 10L.fixed()
      }) {
         ++attempts == 100
      }

      attempts shouldBe 100
   }

   test("until with predicate") {
      var attempts = 0
      var t = ""
      until(5.seconds) {
         attempts++
         t += "x"
         t == "xxx"
      }
      attempts shouldBe 3
   }

   test("until with predicate and interval") {
      measureTime {
         var attempts = 0
         var t = ""
         until({
            duration = 1.seconds.inWholeMilliseconds
            interval = 10L.fixed()
         }) {
            attempts++
            t += "x"
            t == "xxxx"
         }

         attempts shouldBe 4
      }.shouldBeLessThan(100.milliseconds)
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
