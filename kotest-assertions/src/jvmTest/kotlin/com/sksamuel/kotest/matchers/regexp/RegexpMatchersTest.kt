package com.sksamuel.kotest.matchers.regexp

import io.kotest.matchers.regexp.shouldBeRegex
import io.kotest.matchers.regexp.shouldNotBeRegex
import io.kotest.specs.FreeSpec

class RegexpMatchersTest : FreeSpec() {
   init {
      "regexp of same pattern without any option should be same" {
         "a*.js".toRegex() shouldBeRegex "a*.js".toRegex()
      }

      "regexp of different pattern should be different" {
         "a*.js".toRegex() shouldNotBeRegex "bcs.js".toRegex()
      }

      "regexp of same pattern with same option should be same" {
         "a*.js".toRegex(RegexOption.IGNORE_CASE) shouldBeRegex "a*.js".toRegex(RegexOption.IGNORE_CASE)
      }

      "regexp of same pattern with different option should be different" {
         "a*.js".toRegex(RegexOption.IGNORE_CASE) shouldNotBeRegex "a*.js".toRegex()
      }
   }
}
