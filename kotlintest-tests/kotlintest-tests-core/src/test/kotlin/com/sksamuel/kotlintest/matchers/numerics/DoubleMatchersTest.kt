package com.sksamuel.kotlintest.matchers.numerics

import io.kotlintest.matchers.between
import io.kotlintest.matchers.doubles.shouldBeNegative
import io.kotlintest.matchers.doubles.shouldBePositive
import io.kotlintest.matchers.exactly
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.forAll
import io.kotlintest.tables.forNone
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table

class DoubleMatchersTest : ShouldSpec() {
  init {

    should("fail outside of tolerance") {
      shouldThrow<AssertionError> {
        1.0 shouldBe (1.3 plusOrMinus 0.2)
      }
    }

    should("match within tolerance") {
      1.0 shouldBe (1.1 plusOrMinus 0.2)
    }

    should("match exactly without tolerance") {
      1.0 shouldBe 1.0
    }

    should("accept nullable arguments") {
      val l: Double? = 1.0
      val r: Double? = 1.0
      l shouldBe r
    }

    should("match exactly") {
      1.0 shouldBe exactly(1.0)
      shouldThrow<AssertionError> {
        1.0 shouldBe exactly(1.1)
      }
    }

    should("never match NaN == Nan as per the spec") {
      shouldThrow<AssertionError> {
        Double.NaN shouldBe Double.NaN
      }
    }

    should("be positive") {
      1.0.shouldBePositive()

      shouldThrow<AssertionError> {
        (-1.0).shouldBePositive()
      }.message shouldBe "-1.0 should be > 0.0"

      shouldThrow<AssertionError> {
        (0.0).shouldBePositive()
      }.message shouldBe "0.0 should be > 0.0"
    }


    should("be negative") {
      (-1.0).shouldBeNegative()

      shouldThrow<AssertionError> {
        1.0.shouldBeNegative()
      }.message shouldBe "1.0 should be < 0.0"

      shouldThrow<AssertionError> {
        0.0.shouldBeNegative()
      }.message shouldBe "0.0 should be < 0.0"
    }

    should("match between") {
        val table = table(
            headers("a", "b", "tolerance"),
            row(0.0, 2.0, 0.1),
            row(1.0, 2.0, 0.1),
            row(0.0, 1.0, 0.1),
            row(1.0, 1.0, 0.1)

        )
      forAll(table) { a, b, tol ->
        1.0 shouldBe between(a, b, tol)
      }
    }

    should ("never match outside of range") {
         val table = table(
             headers("a", "b"),
             row(0.0, 2.0),
             row(2.0, 2.0),
             row(4.0, 5.0),
             row(4.0, 6.0)
         )

      forNone(table) { a, b ->
        3.0 shouldBe between(a, b, 0.0)
      }
    }
  }
}
