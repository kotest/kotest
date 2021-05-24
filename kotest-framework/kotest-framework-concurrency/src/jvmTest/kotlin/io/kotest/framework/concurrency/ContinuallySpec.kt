package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.time.Duration

private fun Int.hours(): Long = Duration.ofDays(this.toLong()).toMillis()
private fun Int.seconds(): Long = Duration.ofSeconds(this.toLong()).toMillis()
private fun Int.milliseconds(): Long = this.toLong()

@ExperimentalKotest
class ContinuallySpec : FunSpec({
   test("continually passes working tests") {
      continually(500.milliseconds()) {
         (System.currentTimeMillis() > 0) shouldBe true
      }
   }

   test("continually fails broken tests immediately") {
      shouldThrowAny {
         continually(12.hours()) {
            false shouldBe true
         }
      }
   }

   test("continually throws the underlying error") {
      shouldThrowExactly<AssertionError> {
         continually(12.hours()) {
            throw AssertionError("boom")
         }
      }.message shouldBe "boom"
   }

   test("continually fails tests that start off as passing then fail within the period") {
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
})
