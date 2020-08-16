package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.haveMaxLength
import io.kotest.matchers.string.shouldHaveMaxLength
import io.kotest.matchers.string.shouldNotHaveMaxLength

class MaxLengthMatcherTest : FreeSpec() {
   init {
      "should have max length" - {
         "should check max length" {
            "" should haveMaxLength(0)
            "1" should haveMaxLength(1)
            "123" shouldHaveMaxLength 10
            "123" shouldNotHaveMaxLength 1

            shouldThrow<AssertionError> {
               "12" should haveMaxLength(1)
            }.message shouldBe "\"12\" should have maximum length of 1"
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should haveMaxLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot haveMaxLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldHaveMaxLength 0
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotHaveMaxLength 0
            }.message shouldBe "Expecting actual not to be null"
         }
      }
   }
}
