package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.haveSubstring
import io.kotest.matchers.string.shouldInclude
import io.kotest.matchers.string.shouldNotInclude
import io.kotest.matchers.string.include
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain

class IncludeMatcherTest : FreeSpec() {
   init {

      "string should contain" - {
         "should test that a string contains substring" {
            "hello" should include("h")
            "hello" shouldInclude "o"
            "hello" should include("ell")
            "hello" should include("hello")
            "hello" should include("")
            "la tour" shouldContain "tour"

            shouldThrow<AssertionError> {
               "la tour" shouldContain "wibble"
            }.message shouldBe "\"la tour\" should include substring \"wibble\""

            shouldThrow<AssertionError> {
               "hello" should include("allo")
            }.message shouldBe "\"hello\" should include substring \"allo\""

            shouldThrow<AssertionError> {
               "hello" shouldInclude "qwe"
            }.message shouldBe "\"hello\" should include substring \"qwe\""
         }

         "should find a submatch for reasonably long value and substring" {
            val line = "The quick brown fox jumps over the lazy dog"
            shouldThrow<AssertionError> {
               line shouldInclude "The coyote jumps over the lazy dog"
            }.message.shouldContainInOrder(
               """"The quick brown fox jumps over the lazy dog" should include substring "The coyote jumps over the lazy dog"""",
               """Match[0]: expected[10..33] matched actual[19..42]""",
               """Line[0] ="The quick brown fox jumps over the lazy dog"""",
               """Match[0]= -------------------++++++++++++++++++++++++"""
            )
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot include("allo")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotInclude "qwe"
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot contain("allo")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotContain "qwe"
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should include("allo")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should haveSubstring("allo")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldInclude "qwe"
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "Matchers should include substring x" - {
         "should test string contains substring" {
            "bibble" should include("")
            "bibble" should include("bb")
            "bibble" should include("bibble")
         }
         "should fail if string does not contains substring" {
            shouldThrow<AssertionError> {
               "bibble" should include("qweqwe")
            }
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should include("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot include("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldInclude ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotInclude ""
            }.message shouldBe "Expecting actual not to be null"
         }
      }
   }
}
