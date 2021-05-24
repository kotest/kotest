package com.sksamuel.kotest.assertions.timing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.timing.continually
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.hours

class ContinuallyTest : WordSpec() {

  init {
    "continually" should {
      "pass working tests" {
          continually(Duration.milliseconds(500)) {
              (System.currentTimeMillis() > 0) shouldBe true
          }
      }
      "fail broken tests immediately"  {
        shouldThrowAny {
          continually(Duration.hours(12)) {
            false shouldBe true
          }
        }
      }
      "fail should throw the underlying error" {
        shouldThrowExactly<AssertionError> {
          continually(Duration.hours(12)) {
            throw AssertionError("boom")
          }
        }.message shouldBe "boom"
      }
      "fail tests start off as passing then fail within the period" {
        var n = 0
        val e = shouldThrow<Throwable> {

            continually(Duration.seconds(3)) {
                Thread.sleep(5)
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
