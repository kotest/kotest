@file:Suppress("DEPRECATION")

package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.common.ExperimentalKotest
import io.kotest.common.nonConstantTrue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalKotest
class ContinuallySpec : FunSpec({
   test("continually passes working tests") {
      continually(100.milliseconds) {
         nonConstantTrue() shouldBe true
      }
   }

   test("continually fails broken tests immediately") {
      shouldThrowAny {
         continually(12.hours) {
            false shouldBe true
         }
      }
   }

   test("continually throws the underlying error") {
      shouldThrowExactly<AssertionError> {
         continually<Nothing>(12.hours) {
            throw AssertionError("boom")
         }
      }.message shouldBe "boom"
   }

   test("continually fails tests that start off as passing then fail before the time is up") {
      var n = 0
      val e = shouldThrow<Throwable> {
         continually(12.hours) {
            (n++ < 10) shouldBe true
         }
      }

      e.shouldHaveMessage("Test failed after \\d+(\\.\\d*)?\\w+; expected to pass for 12h; attempted 10 times\nUnderlying failure was: expected:<true> but was:<false>".toRegex())
   }
})
