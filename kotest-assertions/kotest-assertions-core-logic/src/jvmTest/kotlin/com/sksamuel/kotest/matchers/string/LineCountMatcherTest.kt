package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.haveLineCount
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldNotHaveLineCount

class LineCountMatcherTest : FreeSpec() {
   init {
      "should have line count" - {
         "should count all newlines" {
            "" should haveLineCount(0)
            "".shouldHaveLineCount(0)
            "\n" should haveLineCount(2)
            "\n" shouldHaveLineCount (2)
            "\r\n" should haveLineCount(2)
            "\r\n".shouldHaveLineCount(2)
            "a\nb\nc" should haveLineCount(3)
            "\r\n" shouldNotHaveLineCount 1
            "\r\n".shouldNotHaveLineCount(3)
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should haveLineCount(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot haveLineCount(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldHaveLineCount 0
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotHaveLineCount 0
            }.message shouldBe "Expecting actual not to be null"
         }
      }
   }
}
