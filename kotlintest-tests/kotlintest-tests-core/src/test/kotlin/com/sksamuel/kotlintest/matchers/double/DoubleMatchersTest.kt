package com.sksamuel.kotlintest.matchers.double

import io.kotlintest.matchers.doubles.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table

class DoubleMatchersTest : ShouldSpec() {
    init {

        should("Not be positive") {
            val table = table(
                    headers("a"),
                    row(-1.0),
                    row(-0.00001),
                    row(-42.0),
                    row(0.0),
                    row(Double.NEGATIVE_INFINITY)
            )

            forAll(table) {
                it.shouldNotBePositive()
                it shouldNotBe positive()
            }

            shouldThrow<AssertionError> {
                0.1.shouldNotBePositive()
                0.1 shouldNotBe positive()

                1.0.shouldNotBePositive()
                1.0 shouldNotBe positive()

                // NAN shouldn't be not positive
                Double.NaN shouldBe positive()
                Double.NaN.shouldBePositive()

                // MAX_VALUE shouldn't be not positive
                Double.MAX_VALUE shouldBe positive()
                Double.MAX_VALUE.shouldBePositive()

                // MIN_VALUE shouldn't be not positive
                Double.MIN_VALUE shouldBe positive()
                Double.MIN_VALUE.shouldBePositive()

                // Positive Infinity shouldn't be not positive
                Double.POSITIVE_INFINITY shouldBe positive()
                Double.POSITIVE_INFINITY.shouldBePositive()
            }
        }

        should("Not be negative") {
            val table = table(
                    headers("a"),
                    row(1.0),
                    row(Double.MIN_VALUE),
                    row(Double.MAX_VALUE),
                    row(0.001),
                    row(0.0),
                    row(Double.POSITIVE_INFINITY)
            )

            forAll(table) {
                it.shouldNotBeNegative()
                it shouldNotBe negative()
            }

            shouldThrow<AssertionError> {
                (-0.1).shouldNotBeNegative()
                -0.1 shouldNotBe negative()

                (-1.0).shouldNotBeNegative()
                -1.0 shouldNotBe negative()

                // NaN shouldn't be not negative
                Double.NaN shouldNotBe negative()
                Double.NaN.shouldNotBeNegative()

                // Positive Infinity shouldn't be not negative
                Double.NEGATIVE_INFINITY.shouldBeNegative()
                Double.NEGATIVE_INFINITY shouldBe negative()
            }
        }
    }
}