package com.sksamuel.kt.properties.shrinking

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.shrinking.StringShrinker
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import shrink

class StringShrinkerTest : StringSpec({

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

  "StringShrinker should shrink to expected value" {
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
