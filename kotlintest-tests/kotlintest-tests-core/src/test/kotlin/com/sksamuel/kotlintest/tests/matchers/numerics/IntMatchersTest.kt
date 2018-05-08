package com.sksamuel.kotlintest.tests.matchers.numerics

import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.beGreaterThanOrEqualTo
import io.kotlintest.matchers.beLessThan
import io.kotlintest.matchers.beLessThanOrEqualTo
import io.kotlintest.matchers.between
import io.kotlintest.matchers.lt
import io.kotlintest.matchers.lte
import io.kotlintest.matchers.numerics.beInRange
import io.kotlintest.matchers.numerics.beEven
import io.kotlintest.matchers.numerics.beOdd
import io.kotlintest.should
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.kotlintest.matchers.shouldBe as shouldBe2
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.forAll
import io.kotlintest.tables.forNone
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table

class IntMatchersTest : StringSpec() {
  init {

    "should return expected/actual in intellij format" {
      shouldThrow<AssertionError> {
       1 shouldBe 444
      }.message shouldBe "expected: 444 but was: 1"
    }

    "shouldBe should support ints" {
      1 shouldBe 1
      2 shouldBe2 2
    }

    "isEven" {
      4 shouldBe beEven()
      3 shouldNotBe beEven()
    }

    "isOdd" {
      3 shouldBe beOdd()
      4 shouldNotBe beOdd()
    }

    "inRange" {
      3 should beInRange(1..10)
      3 should beInRange(3..10)
      3 should beInRange(3..3)
      4 shouldNot beInRange(3..3)
      4 shouldNot beInRange(1..3)
    }

    "beGreaterThan" {
      1 should beGreaterThan(0)

      shouldThrow<AssertionError> {
        2 should beGreaterThan(3)
      }
    }

    "beLessThan" {
      1 should beLessThan(2)

      shouldThrow<AssertionError> {
        2 shouldBe lt(1)
      }
    }

    "beLessThanOrEqualTo" {
      1 should beLessThanOrEqualTo(2)

      shouldThrow<AssertionError> {
        2 shouldBe lte(1)
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