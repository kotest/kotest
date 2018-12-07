package com.sksamuel.kt.properties.shrinking

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldHaveElementAt
import io.kotlintest.matchers.collections.shouldHaveSingleElement
import io.kotlintest.matchers.collections.shouldNotContain
import io.kotlintest.properties.shrinking.IntShrinker
import io.kotlintest.specs.WordSpec

class IntShrinkerTest : WordSpec({
  "IntShrinker" should {
    "return empty list for zero" {
      IntShrinker.shrink(0).shouldBeEmpty()
    }
    "include zero for 1 or -1" {
      IntShrinker.shrink(1).shouldHaveSingleElement(0)
      IntShrinker.shrink(-1).shouldHaveSingleElement(0)
    }
    "include zero as the first candidate" {
      IntShrinker.shrink(55).shouldHaveElementAt(0, 0)
    }
    "include fiver smaller elements" {
      IntShrinker.shrink(55).shouldContainAll(50, 51, 52, 53, 54)
    }
    "include fiver smaller elements unless smaller than zero" {
      IntShrinker.shrink(2).shouldNotContain(-2)
    }
    "include abs value for negative" {
      IntShrinker.shrink(-55).shouldContain(55)
      IntShrinker.shrink(55).shouldNotContain(55)
    }
    "include 1 and negative 1" {
      val candidates = IntShrinker.shrink(56)
      candidates.shouldContainAll(1, -1)
    }
    "include 1/3" {
      val candidates = IntShrinker.shrink(90)
      candidates.shouldContain(30)
    }
    "include 1/2" {
      val candidates = IntShrinker.shrink(90)
      candidates.shouldContain(45)
    }
    "include 2/3" {
      val candidates = IntShrinker.shrink(90)
      candidates.shouldContain(60)
    }
  }
})