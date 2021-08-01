package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.Duration

private fun Int.hours(): Long = Duration.ofDays(this.toLong()).toMillis()
private fun Int.milliseconds(): Long = this.toLong()

@ExperimentalKotest
class ContinuallySpec : FunSpec({
   test("continually passes working tests") {
      continually(100.milliseconds()) {
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

   test("continually fails tests that start off as passing then fail before the time is up") {
      var n = 0
      val e = shouldThrow<Throwable> {

         continually(12.hours()) {
            (n++ < 10) shouldBe true
         }
      }

      e.shouldHaveMessage("Test failed after \\d+ms; expected to pass for \\d+ms; attempted 10 times\nUnderlying failure was: expected:<true> but was:<false>".toRegex())
   }
})
