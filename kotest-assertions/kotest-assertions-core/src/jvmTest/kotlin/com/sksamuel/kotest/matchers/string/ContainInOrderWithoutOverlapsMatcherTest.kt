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
            shouldThrowAny {
               "The quick brown fox jumps over the lazy dog".shouldContainInOrderWithoutOverlaps(
                  "The", "quick", "red", "fox", "jumps", "over", "the", "lazy", "dog"
               )
            }.message.shouldContain("""Did not match substring[2]: <"red">""")
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
               message.shouldContain("""Did not match substring[6]: <"quick brown">""")
               message.shouldContain("Match[0]: whole slice matched actual[4..14]")
               message.shouldContain("""Line[0] ="The quick brown fox jumps over the lazy dog"""")
               message.shouldContain(  "Match[0]= ----+++++++++++----------------------------")
            }
         }
      }
   }
}
