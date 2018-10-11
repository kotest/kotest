package com.sksamuel.kotlintest.matchers.doubles

import io.kotlintest.matchers.doubles.*
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.*

class DoubleMatchersTest : ShouldSpec() {
    init {

        should("Not be positive") {
            val acceptedValues = table(
                    headers("a"),
                    row(-1.0),
                    row(-0.00001),
                    row(-42.0),
                    row(0.0),
                    row(Double.NaN),
                    row(Double.NEGATIVE_INFINITY)
            )

            forAll(acceptedValues) {
                it.shouldNotBePositive()
                it shouldNotBe positive()
            }

            val notAcceptedValues = table(
                    headers("a"),
                    row(0.1),
                    row(1.0),
                    row(Double.MAX_VALUE),
                    row(Double.MIN_VALUE),
                    row(Double.POSITIVE_INFINITY)
            )

            forNone(notAcceptedValues) {
                it.shouldNotBePositive()
                it shouldNotBe positive()
            }
        }

        should("Not be negative") {
            val acceptedValues = table(
                    headers("a"),
                    row(1.0),
                    row(Double.MIN_VALUE),
                    row(Double.MAX_VALUE),
                    row(0.001),
                    row(0.0),
                    row(Double.NaN),
                    row(Double.POSITIVE_INFINITY)
            )

            forAll(acceptedValues) {
                it.shouldNotBeNegative()
                it shouldNotBe negative()
            }

            val notAcceptedValues = table(
                    headers("a"),
                    row(-0.1),
                    row(-1.0),
                    row(Double.NEGATIVE_INFINITY)
            )

            forNone(notAcceptedValues) {
                it.shouldNotBeNegative()
                it shouldNotBe negative()
            }
        }
    }
}