package com.sksamuel.kotest.matchers.regex

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.regex.*
import io.kotest.matchers.regex.shouldEqualRegex
import io.kotest.matchers.regex.shouldNotEqualRegex
import io.kotest.matchers.shouldBe
import kotlin.text.RegexOption.*

class RegexMatchersTest : FreeSpec() {
   init {
      "regex of same pattern without any option should be same" {
         "a*.js".toRegex() shouldBe "a*.js".toRegex()
      }

      "regex of different pattern should be different" {
         "a*.js".toRegex() shouldNotEqualRegex "bcs.js".toRegex()
      }

      "regex of same pattern with same option should be same" {
         "a*.js".toRegex(IGNORE_CASE) shouldEqualRegex "a*.js".toRegex(IGNORE_CASE)
      }

      "regex of same pattern with different option should be different" {
         "a*.js".toRegex(IGNORE_CASE) shouldNotEqualRegex "a*.js".toRegex()
      }

      "regex assertion failure have proper failure message" {
         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldEqualRegex "b*.js".toRegex()
         }.message shouldBe "Regex should have pattern b*.js and regex options [], but has pattern a*.js and regex options []."

         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldEqualRegex "b*.js".toRegex(IGNORE_CASE)
         }.message shouldBe "Regex should have pattern b*.js and regex options [IGNORE_CASE], but has pattern a*.js and regex options []."

         shouldThrow<AssertionError> {
            "a*.js".toRegex(IGNORE_CASE) shouldNotEqualRegex "a*.js".toRegex(IGNORE_CASE)
         }.message shouldBe "Regex should not have pattern a*.js and regex options [IGNORE_CASE]."
      }

      "assert regex is of given pattern" {
         "a.*.js".toRegex() shouldHavePattern "a.*.js"
      }

      "assert regex is not of given pattern" {
         "a.*.js".toRegex() shouldNotHavePattern "bca.js"
      }

      "assert regex have exact given regex options" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldHaveExactRegexOptions setOf(IGNORE_CASE, CANON_EQ)
      }

      "assert regex does not have exact given regex options" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldNotHaveExactRegexOptions  setOf(IGNORE_CASE)
      }

      "assert regex have given regex option" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldIncludeRegexOption  IGNORE_CASE
      }

      "assert regex does not have given regex option" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldNotIncludeRegexOption  COMMENTS
      }

      "assert regex contains all given regex options" {
         "a.js".toRegex(setOf(COMMENTS, IGNORE_CASE, CANON_EQ)) shouldIncludeRegexOptions setOf(IGNORE_CASE, CANON_EQ)
      }

      "assert regex does not contains all given regex options" {
         "a.js".toRegex(setOf(COMMENTS, IGNORE_CASE, CANON_EQ)) shouldNotIncludeRegexOptions setOf(IGNORE_CASE, DOT_MATCHES_ALL)
      }
   }
}
