package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.haveMinLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldNotHaveMinLength

class MinLengthMatcherTest : FreeSpec() {
   init {
      "should have min length" - {
         "should check min length" {
            "" should haveMinLength(0)
            "1" should haveMinLength(1)
            "123" shouldHaveMinLength 1
            "" shouldNotHaveMinLength 1

            shouldThrow<AssertionError> {
               "1" should haveMinLength(2)
            }.message shouldBe "\"1\" should have minimum length of 2"
         }
         "should work on char seq" {
            val empty: CharSequence = ""
            val single: CharSequence = "x"
            val double: CharSequence = "xx"
            empty should haveMinLength(0)
            empty.shouldNotHaveMinLength(1)
            single.shouldHaveMinLength(0)
            single.shouldHaveMinLength(1)
            double.shouldHaveMinLength(2)
            double.shouldNotHaveMinLength(12)
         }
         "should work for nullable char seqs" {
            val cs: CharSequence? = null
            shouldThrow<AssertionError> {
               cs.shouldHaveMinLength(4)
            }.message shouldBe "Expecting actual not to be null"
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should haveMinLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot haveMinLength(0)
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldHaveMinLength 0
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotHaveMinLength 0
            }.message shouldBe "Expecting actual not to be null"
         }
      }   }
}
