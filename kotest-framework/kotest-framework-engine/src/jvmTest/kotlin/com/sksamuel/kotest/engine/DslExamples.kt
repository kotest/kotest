package com.sksamuel.kotest.engine

import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.or
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

class DslExamples {
  init {

    // shouldBe is used when we want to compare two values are equal
    "some string" shouldBe "some" + " " + "string"
    (1 > 0) shouldBe true
    100.0 shouldBe 50 + 50.0

    // shouldNotBe is used when we want to assert values are not equal
    "some string" shouldNotBe "wibble"
    true shouldNotBe false
    100.0 shouldNotBe 50 + 25.0

    // should is another variant on shouldBe which accepts
    // instances of [io.kotest.Matcher]
    // rather than plain values
    56 should beLessThan(4)

    // shouldNot is used when we want to invert a matcher,
    // allowing any matcher to be used in the opposite way.
    100 shouldNot beLessThan(10)

    // we can combine matchers, such that either must pass
    // similar to 'or' in normal boolean alegbra
    50 should (beLessThan(10) or beGreaterThan(40))

    // which again can be used with shouldNot
    50 shouldNot (beLessThan(40) or beGreaterThan(60))

    // there are other variants on should which can be used when
    // you want the assertion to read more elegantly
    // these simply delegate to should so offer nothing in terms
    // of functionality, just simply an alternative vocabularly
    listOf(1, 2, 3) shouldHave singleElement(4)
  }
}
