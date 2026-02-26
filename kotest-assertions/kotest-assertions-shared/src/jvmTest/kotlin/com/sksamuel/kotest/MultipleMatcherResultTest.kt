package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

class MultipleMatcherResultTest : FunSpec() {
   init {

      val passing = MatcherResult(true, { "should equal foo" }, { "should not equal foo" })
      val failingA = MatcherResult(false, { "should equal bar" }, { "should not equal bar" })
      val failingB = MatcherResult(false, { "should equal baz" }, { "should not equal baz" })

      test("passed() returns true when all results pass") {
         MatcherResult.multiple(passing, passing).passed() shouldBe true
      }

      test("passed() returns false when one result fails") {
         MatcherResult.multiple(passing, failingA).passed() shouldBe false
      }

      test("passed() returns false when all results fail") {
         MatcherResult.multiple(failingA, failingB).passed() shouldBe false
      }

      test("failureMessage() lists each result when one fails") {
         val result = MatcherResult.multiple(passing, failingA)
         result.failureMessage() shouldBe """
            Matcher failed due to:
            0) should equal foo
            1) should equal bar

         """.trimIndent()
      }

      test("failureMessage() lists each result when all fail") {
         val result = MatcherResult.multiple(failingA, failingB)
         result.failureMessage() shouldBe """
            Matcher failed due to:
            0) should equal bar
            1) should equal baz

         """.trimIndent()
      }

      test("negatedFailureMessage() lists each negated result") {
         val result = MatcherResult.multiple(passing, failingA)
         result.negatedFailureMessage() shouldBe """
            Matcher failed due to:
            0) should not equal foo
            1) should not equal bar

         """.trimIndent()
      }
   }
}
