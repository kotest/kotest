package com.sksamuel.kotest.properties

import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.assertNone
import io.kotest.properties.forAll
import io.kotest.properties.forNone
import io.kotest.properties.int
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class ClassifyTest : StringSpec() {
  init {
    "classify should log passing predicates" {

      forAll(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a % 2 == 0, "even number", "odd number")
        a + a == 2 * a
      }

      forNone(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a % 2 == 0, "even number", "odd number")
        (a + a).toDouble() == 5.6
      }

      assertAll(Gen.string()) { a ->
        classify(a.contains(" "), "has whitespace", "no whitespace")
        // some test
      }

      assertNone(Gen.int()) { a ->
        classify(a == 0, "zero")
        classify(a % 2 == 0, "even number", "odd number")
        a shouldBe 5.5
      }
    }
  }
}
