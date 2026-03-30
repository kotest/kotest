package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.containInOrderWithoutOverlaps
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrderWithoutOverlaps
import io.kotest.matchers.string.shouldNotContainInOrderWithoutOverlaps

class containInOrderWithoutOverlapsMatcherTest : FreeSpec() {
   init {
      "string should containInOrderWithoutOverlaps()" - {
         "should test that a string contains the requested strings" {
            "a" should containInOrderWithoutOverlaps()
            "a" shouldNot containInOrderWithoutOverlaps("d")
            "ab" should containInOrderWithoutOverlaps("a", "b")
            "ab" shouldNot containInOrderWithoutOverlaps("b", "a")
            "azc" should containInOrderWithoutOverlaps("a", "c")
            "zabzc" should containInOrderWithoutOverlaps("ab", "c")
            "a" shouldNot containInOrderWithoutOverlaps("a", "a")
            "aa" should containInOrderWithoutOverlaps("a", "a")
            "azbzbzc" should containInOrderWithoutOverlaps("a", "b", "b", "c")
            "abab" should containInOrderWithoutOverlaps("a", "b", "a", "b")
            "aaa" should containInOrderWithoutOverlaps("aa", "a")
            "" should containInOrderWithoutOverlaps()
            "" shouldNot containInOrderWithoutOverlaps("a")
            "" should containInOrderWithoutOverlaps("")
            "" should containInOrderWithoutOverlaps("", "")
            "ab" should containInOrderWithoutOverlaps("", "a", "", "b", "")
            "superstar".shouldContainInOrderWithoutOverlaps("super", "star")
         }

         "should output first mismatch" {
            val message = shouldThrowAny {
               "The quick brown fox jumps over the lazy dog".shouldContainInOrderWithoutOverlaps(
                  "The", "quick", "red", "fox", "jumps", "over", "the", "lazy", "dog"
               )
            }.message
            message.shouldContain("""The best fit is the subset with the following indexes: [0, 1, -, 3, 4, 5, 6, 7, 8]""")
            message.shouldContain("Element[2] not found")
         }

         "should fail for overlapping substrings" {
            "sourdough bread".shouldNotContainInOrderWithoutOverlaps("bread", "read")
            "superstar".shouldNotContainInOrderWithoutOverlaps("supers", "star")
         }

         "should find first mismatch before its expected place" {
            val message = shouldThrowAny {
               "The quick brown fox jumps over the lazy dog".shouldContainInOrderWithoutOverlaps(
                  "The", "brown", "fox", "jumps", "over", "the", "quick brown", "lazy", "dog"
               )
            }.message
            assertSoftly {
               message.shouldContain("""The best fit is the subset with the following indexes: [0, -, -, -, -, -, 6, 7, 8]""")
               message.shouldContain("Element[1] found at index(es): [10]")
               message.shouldContain("Element[2] found at index(es): [16]")
               message.shouldContain("Element[3] found at index(es): [20]")
               message.shouldContain("Element[4] found at index(es): [26]")
               message.shouldContain("Element[5] found at index(es): [31]")
            }
         }
      }
   }
}
