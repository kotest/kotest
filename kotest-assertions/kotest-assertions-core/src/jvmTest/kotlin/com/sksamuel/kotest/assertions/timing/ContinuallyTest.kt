@file:Suppress("DEPRECATION")

package com.sksamuel.kotest.assertions.timing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.timing.continually
import io.kotest.common.nonConstantFalse
import io.kotest.common.nonConstantTrue
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ContinuallyTest : WordSpec() {

   init {
      coroutineTestScope = true

      "continually" should {
         "pass working tests" {
            continually(500.milliseconds) {
               nonConstantTrue() shouldBe true
            }
         }

         "fail broken tests immediately" {
            shouldThrowAny {
               continually(12.hours) {
                  nonConstantFalse() shouldBe true
               }
            }
         }

         "fail should throw the underlying error" {
            shouldThrowExactly<AssertionError> {
               continually(12.hours) {
                  if (nonConstantTrue()) throw AssertionError("boom")
               }
            }.message shouldBe "boom"
         }

         "fail tests start off as passing then fail within the period" {
            var n = 0
            val e = shouldThrow<Throwable> {
               continually(3.seconds) {
                  delay(3.milliseconds)
                  n++ shouldBeLessThan 100
               }
            }
            val r =
               ("Test failed after [\\d\\.]+\\w; expected to pass for 3s;" +
                  " attempted 100 times\nUnderlying failure was: 100 should be < 100").toRegex()
            e.message shouldMatch r
         }
      }
   }
}
