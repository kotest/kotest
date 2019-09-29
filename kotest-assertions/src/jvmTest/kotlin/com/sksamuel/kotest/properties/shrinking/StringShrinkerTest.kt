package com.sksamuel.kotest.properties.shrinking

import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.properties.Gen
import io.kotest.properties.PropertyTesting
import io.kotest.properties.assertAll
import io.kotest.properties.shrinking.StringShrinker
import io.kotest.properties.shrinking.shrink
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class StringShrinkerTest : StringSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

  "StringShrinker should include empty string as the first candidate" {
    assertAll { a: String ->
      if (a.isNotEmpty())
        StringShrinker.shrink(a)[0].shouldHaveLength(0)
    }
  }

  "StringShrinker should bisect input as 2nd and 4th candidate" {
    assertAll { a: String ->
      if (a.length > 1) {
        val candidates = StringShrinker.shrink(a)
        candidates[1].shouldHaveLength(a.length / 2 + a.length % 2)
        candidates[3].shouldHaveLength(a.length / 2)
      }
    }
  }

  "StringShrinker should include 2 padded 'a's as the 3rd to 5th candidates" {
    assertAll { a: String ->
      if (a.length > 1) {
        val candidates = StringShrinker.shrink(a)
        candidates[2].shouldEndWith("a".repeat(a.length / 2))
        candidates[4].shouldStartWith("a".repeat(a.length / 2))
      }
    }
  }

  "StringShrinker should io.kotest.properties.shrinking.shrink to expected value" {
    assertAll { it: String ->
      val shrunk = shrink(it, Gen.string()) { it.shouldNotContain("#") }
      if (it.contains("#")) {
        shrunk shouldBe "#"
      } else {
        shrunk shouldBe it
      }
    }
  }

  "StringShrinker should prefer padded values" {
    shrink("97asd!@#ASD'''234)*safmasd", Gen.string()) { it.length.shouldBeLessThan(13) } shouldBe "aaaaaaaaaaaaa"
    shrink("97a", Gen.string()) { it.length.shouldBeLessThan(13) } shouldBe "97a"
  }
})
