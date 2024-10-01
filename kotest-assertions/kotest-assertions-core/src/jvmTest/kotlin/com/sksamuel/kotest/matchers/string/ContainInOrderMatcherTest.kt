package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.containInOrder
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContainInOrder

class ContainInOrderMatcherTest : FreeSpec() {
   init {
      "string should containInOrder()" - {
         "should test that a string contains the requested strings" {
            "a" should containInOrder()
            "a" shouldNot containInOrder("d")
            "ab" should containInOrder("a", "b")
            "ab" shouldNot containInOrder("b", "a")
            "azc" should containInOrder("a", "c")
            "zabzc" should containInOrder("ab", "c")
            "a" shouldNot containInOrder("a", "a")
            "aa" should containInOrder("a", "a")
            "azbzbzc" should containInOrder("a", "b", "b", "c")
            "abab" should containInOrder("a", "b", "a", "b")
            "ababa" should containInOrder("aba", "aba")
            "aaa" should containInOrder("aa", "aa")
            "" should containInOrder()
            "" shouldNot containInOrder("a")
            "" should containInOrder("")
            "" should containInOrder("", "")
            "ab" should containInOrder("", "a", "", "b", "")
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot containInOrder("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldNotContainInOrder("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should containInOrder("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldContainInOrder("")
            }.message shouldBe "Expecting actual not to be null"
         }

         "should output first mismatch" {
            shouldThrowAny {
               "The quick brown fox jumps over the lazy dog".shouldContainInOrder(
                  "The", "quick", "red", "fox", "jumps", "over", "the", "lazy", "dog"
               )
            }.message.shouldContain("""Did not match substring[2]: <"red">""")
         }

         "should find first mismatch before its expected place" {
            val message = shouldThrowAny {
               "The quick brown fox jumps over the lazy dog".shouldContainInOrder(
                  "The", "brown", "fox", "jumps", "over", "the", "quick brown", "lazy", "dog"
               )
            }.message
            assertSoftly {
               message.shouldContain("""Did not match substring[6]: <"quick brown">""")
               message.shouldContain("Match[0]: expected[0..10] matched actual[4..14]")
               message.shouldContain("""Line[0] ="The quick brown fox jumps over the lazy dog"""")
               message.shouldContain(  "Match[0]= ----+++++++++++----------------------------")
            }
         }
      }
   }
}
