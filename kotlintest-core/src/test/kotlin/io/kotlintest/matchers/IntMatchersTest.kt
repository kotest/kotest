package io.kotlintest.matchers

import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec

class IntMatchersTest : StringSpec() {
  init {

    "beGreaterThan" {
      1 should beGreaterThan(0)

      shouldThrow<AssertionError> {
        2 should beGreaterThan(3)
      }
    }

    "beLessThan" {
      1 should beLessThan(2)

      shouldThrow<AssertionError> {
        2 should beLessThan(1)
      }
    }

    "beLessThanOrEqualTo" {
      1 should beLessThanOrEqualTo(2)

      shouldThrow<AssertionError> {
        2 should beLessThanOrEqualTo(1)
      }
    }

    "greaterThan" {
      1 should beGreaterThanOrEqualTo(0)

      shouldThrow<AssertionError> {
        2 should beGreaterThanOrEqualTo(3)
      }
    }

    "between should test for valid interval" {

      val table = table(
          headers("a", "b"),
          row(0, 2),
          row(1, 2),
          row(0, 1),
          row(1, 1)
      )

      forAll(table) { a, b ->
        1 shouldBe between(a, b)
      }
    }

    "between should test for invalid interval" {

      val table = table(
          headers("a", "b"),
          row(0, 2),
          row(2, 2),
          row(4, 5),
          row(4, 6)
      )

      forNone(table) { a, b ->
        3 shouldBe between(a, b)
      }
    }
  }
}