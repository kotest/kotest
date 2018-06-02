package com.sksamuel.kotlintest.properties.shrinking

import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.properties.Gen
import io.kotlintest.properties.shrinking.StringShrinker
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import shrink

class StringShrinkerTest : StringSpec({

  "StringShrinker shrinks should include empty string as the first candidate" {
    assertAll { a: String ->
      if (a.isNotEmpty())
        StringShrinker.shrink(a)[0].shouldHaveLength(0)
    }
  }

  "StringShrinker should include 5-1 sizes smaller as the 2nd to 6th candidates" {
    assertAll { a: String ->
      if (a.length > 5) {
        val candidates = StringShrinker.shrink(a)
        candidates[1].shouldHaveLength(a.length - 5)
        candidates[2].shouldHaveLength(a.length - 4)
        candidates[3].shouldHaveLength(a.length - 3)
        candidates[4].shouldHaveLength(a.length - 2)
        candidates[5].shouldHaveLength(a.length - 1)
      }
    }
  }

  "StringShrinker should include 5-1 padded 'a's as the 7th to 11th candidates" {
    assertAll { a: String ->
      if (a.length > 5) {
        val candidates = StringShrinker.shrink(a)
        candidates[6].shouldStartWith("aaaaa")
        candidates[7].shouldStartWith("aaaa")
        candidates[8].shouldStartWith("aaa")
        candidates[9].shouldStartWith("aa")
        candidates[10].shouldStartWith("a")
      }
    }
  }

  "StringShrinker should shrink to expected value" {
    fun pad(str: String) = str.padEnd(12, '*')
    shrink("97asd!@#ASD'''234)*safmasd", Gen.string(), { pad(it).shouldHaveLength(12) }) shouldBe "aaaaaaaaaaaaa"
    shrink("97a", Gen.string(), { pad(it).shouldHaveLength(12) }) shouldBe "97a"
  }
})