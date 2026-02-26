package com.sksamuel.kotest.matchers.regex

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.regex.shouldEqualRegex
import io.kotest.matchers.regex.shouldHaveExactRegexOptions
import io.kotest.matchers.regex.shouldHavePattern
import io.kotest.matchers.regex.shouldIncludeRegexOption
import io.kotest.matchers.regex.shouldIncludeRegexOptions
import io.kotest.matchers.regex.shouldNotEqualRegex
import io.kotest.matchers.regex.shouldNotHaveExactRegexOptions
import io.kotest.matchers.regex.shouldNotHavePattern
import io.kotest.matchers.regex.shouldNotIncludeRegexOption
import io.kotest.matchers.regex.shouldNotIncludeRegexOptions
import io.kotest.matchers.shouldBe
import kotlin.text.RegexOption.IGNORE_CASE
import kotlin.text.RegexOption.CANON_EQ
import kotlin.text.RegexOption.COMMENTS
import kotlin.text.RegexOption.DOT_MATCHES_ALL

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
         }.message shouldBe """Regex should have pattern b*.js but has pattern a*.js"""

         shouldThrow<AssertionError> {
            "a*.js".toRegex() shouldEqualRegex "b*.js".toRegex(IGNORE_CASE)
         }.message shouldBe """Matcher failed due to:
0) Regex should have pattern b*.js but has pattern a*.js
1) Regex should have options [IGNORE_CASE] but has options []
"""

         shouldThrow<AssertionError> {
            "a*.js".toRegex(IGNORE_CASE) shouldNotEqualRegex "a*.js".toRegex(IGNORE_CASE)
         }.message shouldBe """Matcher failed due to:
0) Regex should not have pattern a*.js
1) Regex should not have options [IGNORE_CASE]
"""
      }

      "assert regex is of given pattern" {
         "a.*.js".toRegex() shouldHavePattern "a.*.js"
      }

      "assert regex is not of given pattern" {
         "a.*.js".toRegex() shouldNotHavePattern "bca.js"
      }

      "regex options should work for empty set" {
         "a.*.js".toRegex(setOf()) shouldHaveExactRegexOptions emptySet()
      }

      "assert regex have exact given regex options" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldHaveExactRegexOptions setOf(IGNORE_CASE, CANON_EQ)
      }

      "assert regex does not have exact given regex options" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldNotHaveExactRegexOptions setOf(IGNORE_CASE)
      }

      "assert regex have given regex option" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldIncludeRegexOption IGNORE_CASE
      }

      "assert regex does not have given regex option" {
         "a.*.js".toRegex(setOf(IGNORE_CASE, CANON_EQ)) shouldNotIncludeRegexOption COMMENTS
      }

      "assert regex contains all given regex options" {
         "a.js".toRegex(setOf(COMMENTS, IGNORE_CASE, CANON_EQ)) shouldIncludeRegexOptions setOf(IGNORE_CASE, CANON_EQ)
      }

      "assert regex does not contains all given regex options" {
         "a.js".toRegex(setOf(COMMENTS, IGNORE_CASE, CANON_EQ)) shouldNotIncludeRegexOptions setOf(
            IGNORE_CASE,
            DOT_MATCHES_ALL
         )
      }
   }
}
