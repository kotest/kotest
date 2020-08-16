package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotEndWith

class EndWithMatcherTest : FreeSpec() {
   init {
      "should endWith" - {
         "should test strings" {
            "hello" should endWith("o")
            "hello" should endWith("")
            "hello" shouldEndWith ""
            "hello" shouldEndWith "lo"
            "hello" shouldEndWith "o"
            "hello" shouldNotEndWith "w"
            "" should endWith("")
            shouldThrow<AssertionError> {
               "" should endWith("h")
            }
            shouldThrow<AssertionError> {
               "hello" should endWith("goodbye")
            }
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot endWith("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotEndWith ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should endWith("o")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldEndWith "o"
            }.message shouldBe "Expecting actual not to be null"
         }
      }
   }
}
