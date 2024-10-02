package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.string.startWith

class StartWithTest : FreeSpec() {
   init {
      "should startWith" - {
         "should test strings" {
            "hello" should startWith("h")
            "hello" should startWith("h" as CharSequence)
            "hello" should startWith("")
            "hello" shouldStartWith ""
            "hello" shouldStartWith "h"
            "hello" shouldStartWith "he"
            "hello" shouldStartWith "(he|hi)".toRegex()
            "hello" shouldNotStartWith "w"
            "hello" shouldNotStartWith "wo"
            "" should startWith("")
            shouldThrow<AssertionError> {
               "" should startWith("h")
            }
            shouldThrow<AssertionError> {
               "hello" should startWith("goodbye")
            }
            shouldThrow<AssertionError> {
               "hello" should startWith("(el|lo)".toRegex())
            }
         }
         "work with char seqs" {
            val cs: CharSequence = "hello"
            cs should startWith("h")
            cs.shouldStartWith("h")

            val csnullable: CharSequence? = "hello"
            csnullable should startWith("h")
            csnullable.shouldStartWith("h")
         }
         "return the correct type" {
            val cs1: CharSequence = "hello"
            val a1 = cs1.shouldStartWith("h")
            a1 shouldBe "hello"

            val cs2: CharSequence? = "hello"
            val a2 = cs2.shouldStartWith("h")
            a2 shouldBe "hello"
         }
         "should show divergence in error message" {
            val message = shouldThrow<AssertionError> {
               "la tour eiffel" should startWith("la tour tower london")
            }.message
            assertSoftly {
               message shouldStartWith "\"la tour eiffel\" should start with \"la tour tower london\" (diverged at index 8)"
               message.shouldContainInOrder(
                  "Match[0]: expected[0..7] matched actual[0..7]",
                  """Line[0] ="la tour eiffel"""",
                  """Match[0]= ++++++++------"""
               )
            }
         }
         "should find prefix in the middle of the string" {
            val message = shouldThrow<AssertionError> {
               "The quick brown fox jumps over the lazy dog" should startWith("quick brown fox jumps")
            }.message
            assertSoftly {
               message shouldStartWith """"The quick brown fox jumps over the lazy dog" should start with "quick brown fox jumps" (diverged at index 0)"""
               message.shouldContainInOrder(
                  "Match[0]: expected[0..20] matched actual[4..24]",
                  """Line[0] ="The quick brown fox jumps over the lazy dog"""",
                  """Match[0]= ----+++++++++++++++++++++------------------"""
               )
            }
         }
         "should show should start with regex in error message" {
            shouldThrow<AssertionError> {
               "hello" should startWith("(e|lo)".toRegex())
            }.message shouldBe "\"hello\" should start with regex (e|lo)"
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should startWith("h")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldStartWith ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot startWith("h")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotStartWith "w"
            }.message shouldBe "Expecting actual not to be null"
         }
      }
   }
}
