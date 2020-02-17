package com.sksamuel.kotest.assertions.timing

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.timing.continually
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.seconds

@UseExperimental(ExperimentalTime::class)
class ContinuallyTest : WordSpec() {

   init {
      "continually" should {
         "pass working tests" {
            continually(500.milliseconds) {
               (3 < 4) shouldBe true
            }
         }
         "fail broken tests immediately"  {
            shouldThrowExactly<AssertionError> {
               continually(12.hours) {
                  (5 < 4) shouldBe true
               }
            }.message shouldBe "5 should be < 4"
         }
         "fail tests start off as passing then fail within the period" {
            var n = 0
            val e = shouldThrowExactly<AssertionError> {
               continually(3.seconds) {
                  Thread.sleep(10)
                  (n++ < 100) shouldBe true
               }
            }
            val r =
               "Test failed after [\\d]+ms; expected to pass for 3000ms; attempted 100 times\nUnderlying failure was: 100 should be < 100".toRegex()
            e.message?.matches(r) ?: false shouldBe true
         }
      }
   }
}
