package com.sksamuel.kotlintest.tests.properties

import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.assertNone
import io.kotlintest.properties.forAll
import io.kotlintest.properties.forNone
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ClassifyTest : StringSpec() {
  init {
    "classify should log passing predicates" {

      forAll(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a > 0, "positive number", "negative number")
        classify(a % 2 == 0, "even number", "odd number")
        a + a == 2 * a
      }

      forNone(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a > 0, "positive number", "negative number")
        classify(a % 2 == 0, "even number", "odd number")
        (a + a).toDouble() == 5.6
      }

      assertAll(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a > 0, "positive number", "negative number")
        classify(a % 2 == 0, "even number", "odd number")
      }

      assertNone(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a > 0, "positive number", "negative number")
        classify(a % 2 == 0, "even number", "odd number")
        a shouldBe 5.5
      }
    }
  }
}