package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.string.startWith

class StartWithMatcherTest : FreeSpec() {
   init {
      "should startWith" - {
         "should test strings" {
            "hello" should startWith("h")
            "hello" should startWith("")
            "hello" shouldStartWith ""
            "hello" shouldStartWith "h"
            "hello" shouldStartWith "he"
            "hello" shouldNotStartWith "w"
            "hello" shouldNotStartWith "wo"
            "" should startWith("")
            shouldThrow<AssertionError> {
               "" should startWith("h")
            }
            shouldThrow<AssertionError> {
               "hello" should startWith("goodbye")
            }
         }
         "should show divergence in error message" {
            shouldThrow<AssertionError> {
               "la tour eiffel" should startWith("la tour tower london")
            }.message shouldBe "\"la tour eiffel\" should start with \"la tour tower london\" (diverged at index 8)"
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
      }   }
}
