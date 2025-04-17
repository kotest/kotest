package com.sksamuel.kotest.matchers.numerics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beGreaterThanOrEqualTo
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.comparables.beLessThanOrEqualTo
import io.kotest.matchers.longs.shouldBeNegative
import io.kotest.matchers.longs.shouldBePositive
import io.kotest.matchers.longs.shouldBeZero
import io.kotest.matchers.longs.shouldNotBeZero
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.comparables.between
import io.kotest.matchers.comparables.shouldBeBetween

class LongMatchersTest : StringSpec() {
  init {

    "be positive" {
      1L.shouldBePositive()

      shouldThrow<AssertionError> {
        (-1L).shouldBePositive()
      }.message shouldBe "-1 should be > 0"

      shouldThrow<AssertionError> {
        (0L).shouldBePositive()
      }.message shouldBe "0 should be > 0"
    }


    "be negative" {
      (-1L).shouldBeNegative()

      shouldThrow<AssertionError> {
        1L.shouldBeNegative()
      }.message shouldBe "1 should be < 0"

      shouldThrow<AssertionError> {
        0L.shouldBeNegative()
      }.message shouldBe "0 should be < 0"
    }

    "Ge should be valid" {
      1L should beGreaterThan(0L)
    }

    "beGreaterThan" {
      1L should beGreaterThan(0L)

      shouldThrow<AssertionError> {
        2L should beGreaterThan(3L)
      }
    }

    "beLessThan" {
      1L should beLessThan(2L)

      shouldThrow<AssertionError> {
        2L should beLessThan(1L)
      }
    }

    "beLessThanOrEqualTo" {
      1L should beLessThanOrEqualTo(2L)

      shouldThrow<AssertionError> {
        2L should beLessThanOrEqualTo(1L)
      }
    }

    "greaterThan" {
      1L should beGreaterThanOrEqualTo(0L)

      shouldThrow<AssertionError> {
        2L should beGreaterThanOrEqualTo(3L)
      }
    }

    "between should test for valid interval" {

      val table = table(
          headers("a", "b"),
          row(0L, 2L),
          row(1L, 2L),
          row(0L, 1L),
          row(1L, 1L)
      )

      forAll(table) { a, b ->
         1L.shouldBeBetween(a, b)
      }
    }

    "between should test for invalid interval" {

      val table = table(
          headers("a", "b"),
          row(0L, 2L),
          row(2L, 2L),
          row(4L, 5L),
          row(4L, 6L)
      )

      forNone(table) { a, b ->
        3L shouldBe between(a, b)
      }
    }

    "shouldBeZero" {
      (0L).shouldBeZero()
      (1L).shouldNotBeZero()
      Long.MIN_VALUE.shouldNotBeZero()
      Long.MAX_VALUE.shouldNotBeZero()
    }
  }
}
