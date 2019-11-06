package com.sksamuel.kotest.matchers.regex

import io.kotest.matchers.regex.shouldBeRegex
import io.kotest.matchers.regex.shouldNotBeRegex
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec

class RegexMatchersTest : FreeSpec() {
   init {
      "regex of same pattern without any option should be same" {
         "a*.js".toRegex() shouldBeRegex "a*.js".toRegex()
      }

      "regex of different pattern should be different" {
         "a*.js".toRegex() shouldNotBeRegex "bcs.js".toRegex()
      }

      "regex of same pattern with same option should be same" {
         "a*.js".toRegex(RegexOption.IGNORE_CASE) shouldBeRegex "a*.js".toRegex(RegexOption.IGNORE_CASE)
      }

      "regex of same pattern with different option should be different" {
         "a*.js".toRegex(RegexOption.IGNORE_CASE) shouldNotBeRegex "a*.js".toRegex()
      }

      "regex assertion failure have proper failure message" {
         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldBeRegex "b*.js".toRegex()
         }.message shouldBe "Regex should have pattern b*.js and regex options [], but has pattern a*.js and regex options []."

         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldBeRegex "b*.js".toRegex(RegexOption.IGNORE_CASE)
         }.message shouldBe "Regex should have pattern b*.js and regex options [IGNORE_CASE], but has pattern a*.js and regex options []."

         shouldThrow<AssertionError> {
            "a*.js".toRegex(RegexOption.IGNORE_CASE) shouldNotBeRegex "a*.js".toRegex(RegexOption.IGNORE_CASE)
         }.message shouldBe "Regex should not have pattern a*.js and regex options [IGNORE_CASE]."
      }
   }
}
