package com.sksamuel.kotest.matchers

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResultBuilder
import io.kotest.matchers.invokeMatcher
import io.kotest.matchers.shouldBe

class ThrowableMatcherResultTest : FunSpec({

   test("invokeMatcher rethrows the exact throwable supplied via withError") {
      val cause = IllegalStateException("something went wrong")

      val matcher = Matcher<String> { _ ->
         MatcherResultBuilder.create(passed = false)
            .withError(cause)
            .build()
      }

      val thrown = shouldThrowExactly<IllegalStateException> {
         invokeMatcher("value", matcher)
      }

      thrown shouldBe cause
   }

   test("invokeMatcher does not rethrow when passed is true even with withError") {
      val matcher = Matcher<String> { _ ->
         MatcherResultBuilder.create(passed = true)
            .withError(IllegalStateException("should not be thrown"))
            .build()
      }

      // no exception expected — the assertion passed
      invokeMatcher("value", matcher) shouldBe "value"
   }


})
