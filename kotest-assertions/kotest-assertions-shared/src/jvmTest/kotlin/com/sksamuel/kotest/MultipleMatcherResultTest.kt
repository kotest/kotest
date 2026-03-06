package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

class MultipleMatcherResultTest : FunSpec() {
   init {

      val passingA = MatcherResult(true, { "should equal foo" }, { "should not equal foo" })
      val passingB = MatcherResult(true, { "should equal faz" }, { "should not equal faz" })
      val failingA = MatcherResult(false, { "should equal bar" }, { "should not equal bar" })
      val failingB = MatcherResult(false, { "should equal baz" }, { "should not equal baz" })

      test("passed() returns true when all results pass") {
         MatcherResult.multiple(passingA, passingA).passed() shouldBe true
      }

      test("passed() returns false when one result fails") {
         MatcherResult.multiple(passingA, failingA).passed() shouldBe false
      }

      test("passed() returns false when all results fail") {
         MatcherResult.multiple(failingA, failingB).passed() shouldBe false
      }

      test("failureMessage() lists only the failing results when < all fail") {
         val result = MatcherResult.multiple(passingA, failingA, failingB)
         result.failureMessage() shouldBe """
Matcher failed due to:
0) should equal bar
1) should equal baz
""".trim()
      }

      test("failureMessage() lists each result when all fail") {
         val result = MatcherResult.multiple(failingA, failingB)
         result.failureMessage() shouldBe """
Matcher failed due to:
0) should equal bar
1) should equal baz
         """.trim()
      }

      test("negatedFailureMessage() lists each negated result") {
         val result = MatcherResult.multiple(passingA, passingB)
         result.negatedFailureMessage() shouldBe """
Matcher failed due to:
0) should not equal foo
1) should not equal faz
         """.trim()
      }

      test("negatedFailureMessage() lists only the passing results when < all pass") {
         val result = MatcherResult.multiple(passingA, passingB, failingA, failingB)
         result.negatedFailureMessage() shouldBe """
Matcher failed due to:
0) should not equal foo
1) should not equal faz
""".trim()
      }
   }
}
