package com.sksamuel.kotest.matchers.regex

import io.kotest.matchers.regex.*
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec
import kotlin.text.RegexOption.CANON_EQ
import kotlin.text.RegexOption.IGNORE_CASE

class RegexMatchersTest : FreeSpec() {
   init {
      "regex of same pattern without any option should be same" {
         "a*.js".toRegex() shouldBeRegex "a*.js".toRegex()
      }

      "regex of different pattern should be different" {
         "a*.js".toRegex() shouldNotBeRegex "bcs.js".toRegex()
      }

      "regex of same pattern with same option should be same" {
         "a*.js".toRegex(IGNORE_CASE) shouldBeRegex "a*.js".toRegex(IGNORE_CASE)
      }

      "regex of same pattern with different option should be different" {
         "a*.js".toRegex(IGNORE_CASE) shouldNotBeRegex "a*.js".toRegex()
      }

      "regex assertion failure have proper failure message" {
         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldBeRegex "b*.js".toRegex()
         }.message shouldBe "Regex should have pattern b*.js and regex options [], but has pattern a*.js and regex options []."

         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldBeRegex "b*.js".toRegex(IGNORE_CASE)
         }.message shouldBe "Regex should have pattern b*.js and regex options [IGNORE_CASE], but has pattern a*.js and regex options []."

         shouldThrow<AssertionError> {
            "a*.js".toRegex(IGNORE_CASE) shouldNotBeRegex "a*.js".toRegex(IGNORE_CASE)
         }.message shouldBe "Regex should not have pattern a*.js and regex options [IGNORE_CASE]."
      }

      "assert regex is of given pattern" {
         "a.*.js".toRegex() shouldHavePattern "a.*.js"
      }

      "assert regex is not of given pattern" {
         "a.*.js".toRegex() shouldNotHavePattern "bca.js"
      }

      "assert regex have all given regex options" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldHaveAllRegexOptions setOf(IGNORE_CASE, CANON_EQ)
      }

      "assert regex does not all have given regex options" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldNotHaveAllRegexOptions  setOf(IGNORE_CASE)
      }

   }
}
