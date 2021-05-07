package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.time.Duration

private fun Int.hours(): Millis = Duration.ofDays(this.toLong()).toMillis()
private fun Int.seconds(): Millis = Duration.ofSeconds(this.toLong()).toMillis()
private fun Int.milliseconds(): Millis = this.toLong()

@OptIn(ExperimentalKotest::class)
class ContinuallySpec : WordSpec({
   "continually" should {
      "pass working tests" {
         continually(500.milliseconds()) {
            (System.currentTimeMillis() > 0) shouldBe true
         }
      }
      "fail broken tests immediately"  {
         shouldThrowAny {
            continually(12.hours()) {
               false shouldBe true
            }
         }
      }
      "fail should throw the underlying error" {
         shouldThrowExactly<AssertionError> {
            continually(12.hours()) {
               throw AssertionError("boom")
            }
         }.message shouldBe "boom"
      }
      "fail tests start off as passing then fail within the period" {
         var n = 0
         val e = shouldThrow<Throwable> {

            continually(10.seconds()) {
               delay(5L)
               (n++ < 100) shouldBe true
            }
         }
         val r =
            "Test failed after [\\d]+ms; expected to pass for 10000ms; attempted 100 times\nUnderlying failure was: 100 should be < 100".toRegex()
         e.message?.matches(r) ?: false shouldBe true
      }
   }
})
