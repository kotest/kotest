package com.sksamuel.kotest.properties.shrinking

import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldStartWith
import io.kotest.properties.Gen
import io.kotest.properties.PropertyTesting
import io.kotest.properties.assertAll
import io.kotest.properties.char
import io.kotest.properties.choose
import io.kotest.properties.shrinking.StringShrinker
import io.kotest.properties.shrinking.shrink
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.StringSpec

class StringShrinkerTest : StringSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   "StringShrinker should not allow minimum size < 0" {
      assertAll(Gen.choose(-10, -1)) { minSize ->
         shouldThrow<IllegalArgumentException> { StringShrinker(minSize, 'a') }
      }
   }

   "StringShrinker should shrink to minimum size when > 0" {
      StringShrinker(3, '~').shrink("abcdef") shouldBe listOf(
         "abc", "abcde", "abcde~", "abcf", "abc~~f"
      )
   }

   "StringShrinker should shrink to minimum size when is 0" {
      StringShrinker(0, '~').shrink("abcdef") shouldBe listOf(
         "", "abc", "abc~~~", "def", "~~~def"
      )
   }

   "StringShrinker should include empty string as the first candidate when minSize is 0" {
      assertAll(Gen.string(1)) { a: String ->
         StringShrinker(0, 'a').shrink(a)[0].shouldHaveLength(0)
      }
   }

   "StringShrinker should bisect input as 2nd and 4th candidate" {
      assertAll(Gen.string(2)) { a: String ->
         StringShrinker(0, 'a').shrink(a).also { candidates ->
            candidates[1].shouldHaveLength(a.length / 2 + a.length % 2)
            candidates[3].shouldHaveLength(a.length / 2)
         }
      }
   }

   "StringShrinker should include 2 padded 'a's as the 3rd to 5th candidates" {
      assertAll(Gen.string(2)) { a: String ->
         StringShrinker(0, 'a').shrink(a).also { candidates ->
            candidates[2].shouldEndWith("a".repeat(a.length / 2))
            candidates[4].shouldStartWith("a".repeat(a.length / 2))
         }
      }
   }

   "StringShrinker should io.kotest.properties.shrinking.shrink to expected value" {
      assertAll(Gen.string(minSize = 3, genChar = Gen.char('a'..'z'))) { s: String ->
         shrink(s, StringShrinker(0, 'a')) { testValue ->
            testValue.length shouldBe 0
         }.also { shrunk ->
            shrunk shouldBe "a"
         }
      }
   }

   "StringShrinker should prefer padded values" {
      shrink("97asd!@#ASD'''234)*safmasd", StringShrinker(0, 'a')) {
         it.length.shouldBeLessThan(13)
      } shouldBe "aaaaaaaaaaaaa"
      shrink("97a", StringShrinker(0, 'a')) {
         it.length.shouldBeLessThan(13)
      } shouldBe "97a"
   }
})
