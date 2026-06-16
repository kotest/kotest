package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotEndWith

@Suppress("RedundantNullableReturnType")
class EndWithTest : FreeSpec() {
   init {
      "should endWith" - {
         "should test strings" {
            "hello" should endWith("o")
            "hello" should endWith("o" as CharSequence)
            "hello" should endWith("")
            "hello" should endWith("" as CharSequence)
            "hello" shouldEndWith ""
            "hello" shouldEndWith "lo"
            "hello" shouldEndWith "o"
            "hello" shouldNotEndWith "w"
            "hello" shouldEndWith "(lo|sa)".toRegex()
            "" should endWith("")
            shouldThrow<AssertionError> {
               "" should endWith("h")
            }
            shouldThrow<AssertionError> {
               "hello" should endWith("goodbye")
            }
            shouldThrow<AssertionError> {
               "hello" should endWith("(el|lol)".toRegex())
            }
         }
         "find submatch in the middle of value" {
            val message = shouldThrow<AssertionError> {
               "The quick brown fox jumps over the lazy dog" should endWith("fox jumps over the lazy cat")
            }.message
            message.shouldContainInOrder(
               "Match[0]: part of suffix with indexes [0..23] matched actual[16..39]",
               """Line[0] ="The quick brown fox jumps over the lazy dog"""",
               """Match[0]= ----------------++++++++++++++++++++++++---"""
            )
         }
         "work with char seqs" {
            val cs: CharSequence = "hello"
            cs should endWith("o")
            cs.shouldEndWith("o")

            val csnullable: CharSequence? = "hello"
            csnullable should endWith("o")
            csnullable.shouldEndWith("o")
         }
         "return the correct type" {
            val cs1: CharSequence = "hello"
            val a1 = cs1.shouldEndWith("o")
            a1 shouldBe "hello"

            val cs2: CharSequence? = "hello"
            val a2 = cs2.shouldEndWith("o")
            a2 shouldBe "hello"
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
         "should show should end with regex in error message" {
            shouldThrow<AssertionError> {
               "hello" should endWith("(e|ol)".toRegex())
            }.message shouldBe "\"hello\" should end with regex (e|ol)"
         }
      }
   }
}
