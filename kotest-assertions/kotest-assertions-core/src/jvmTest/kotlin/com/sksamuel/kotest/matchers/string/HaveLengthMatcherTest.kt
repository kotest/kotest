package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.haveLength
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotHaveLength

class HaveLengthMatcherTest : FreeSpec() {
   init {
      "should haveLength(5)" - {
         "should compare length of string" {
            "bibble" should haveLength(6)
            "" should haveLength(0)
            "" shouldHaveLength 0
            "hello" shouldNotHaveLength 3
            "hello" shouldHaveLength 5
            shouldThrow<AssertionError> {
               "" should haveLength(3)
            }.message shouldBe "<empty string> should have length 3, but instead was 0"
            shouldThrow<AssertionError> {
               "" shouldHaveLength 3
            }.message shouldBe "<empty string> should have length 3, but instead was 0"
            shouldThrow<AssertionError> {
               "hello" shouldHaveLength 3
            }.message shouldBe "\"hello\" should have length 3, but instead was 5"
            shouldThrow<AssertionError> {
               "hello" shouldNotHaveLength 5
            }.message shouldBe "\"hello\" should not have length 5"
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should haveLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should haveLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot haveLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotHaveLength 0
            }.message shouldBe "Expecting actual not to be null"
         }
      }   }
}
