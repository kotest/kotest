package com.sksamuel.kotest.matchers.doubles

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.doubles.beGreaterThan
import io.kotest.matchers.doubles.beGreaterThanOrEqualTo
import io.kotest.matchers.doubles.beLessThan
import io.kotest.matchers.doubles.beLessThanOrEqualTo
import io.kotest.matchers.doubles.beNaN
import io.kotest.matchers.doubles.beNegativeInfinity
import io.kotest.matchers.doubles.bePositiveInfinity
import io.kotest.matchers.doubles.between
import io.kotest.matchers.doubles.gt
import io.kotest.matchers.doubles.gte
import io.kotest.matchers.doubles.lt
import io.kotest.matchers.doubles.lte
import io.kotest.matchers.doubles.negative
import io.kotest.matchers.doubles.positive
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.doubles.shouldBeMultipleOf
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.doubles.shouldBeNegative
import io.kotest.matchers.doubles.shouldBeNegativeInfinity
import io.kotest.matchers.doubles.shouldBePositive
import io.kotest.matchers.doubles.shouldBePositiveInfinity
import io.kotest.matchers.doubles.shouldBeZero
import io.kotest.matchers.doubles.shouldNotBeBetween
import io.kotest.matchers.doubles.shouldNotBeGreaterThan
import io.kotest.matchers.doubles.shouldNotBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldNotBeLessThan
import io.kotest.matchers.doubles.shouldNotBeLessThanOrEqual
import io.kotest.matchers.doubles.shouldNotBeNaN
import io.kotest.matchers.doubles.shouldNotBeNegative
import io.kotest.matchers.doubles.shouldNotBeNegativeInfinity
import io.kotest.matchers.doubles.shouldNotBePositive
import io.kotest.matchers.doubles.shouldNotBePositiveInfinity
import io.kotest.matchers.doubles.shouldNotBeZero
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll
import kotlin.Double.Companion.MAX_VALUE
import kotlin.Double.Companion.MIN_VALUE
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.NaN
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.absoluteValue

class DoubleMatchersTest : FreeSpec() {

  init {

    "Between matcher" - {

      "Every numeric double that is not Double.MAX_VALUE" - {

        "Should match between" - {

          "When it's equal to the first number of the range" - {

            "With tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it, it.slightlyGreater(), it.toleranceValue())
              }
            }

            "Without tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it, it.slightlyGreater(), 0.0)

              }
            }
          }

          "When it's between the first number of the range and the last one" - {

            "With tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it.slightlyGreater(), it.toleranceValue())
              }
            }

            "Without tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it.slightlyGreater(), 0.0)
              }
            }
          }

          "When it's equal to the last number of the range" - {

            "With tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it, it.toleranceValue())
              }
            }

            "Without tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it, 0.0)
              }
            }
          }
        }

        "Should not match between" - {

          "When it's smaller than the first number of the range" - {

            "With tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.slightlyGreater(), it.muchGreater(), it.toleranceValue())
              }
            }

            "Without tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.slightlyGreater(), it.muchGreater(), 0.0)
              }
            }
          }

          "When it's bigger than the last number of the range" - {

            "With tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.muchSmaller(), it.slightlySmaller(), it.toleranceValue())
              }
            }

            "Without tolerance" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.muchSmaller(), it.slightlySmaller(), 0.0)
              }
            }
          }
        }


      }
    }

    "Less than matcher" - {

      "Every numeric double" - {

        "Should be less than" - {

          "Numbers bigger than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchLessThan it.slightlyGreater()
              it shouldMatchLessThan it.muchGreater()
            }
          }

          "Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchLessThan POSITIVE_INFINITY
            }
          }
        }


        "Should not be less than" - {

          "Itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan it
            }
          }

          "Numbers smaller than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan it.slightlySmaller()
              it shouldNotMatchLessThan it.muchSmaller()
            }
          }

          "Negative Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan it
            }
          }

          "NaN" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan NaN
            }
          }
        }

      }

      "The non-numeric double" - {

        "NaN" - {
          "Should not be less than" - {

            "Any numeric double" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                NaN shouldNotMatchLessThan it
              }
            }

            "Any non-numeric double" {
              nonNumericDoubles.forEach {
                NaN shouldNotMatchLessThan it
              }
            }
          }

        }

        "Positive Infinity" - {
          "Should not be less than" - {

            "Any numeric double" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                POSITIVE_INFINITY shouldNotMatchLessThan it
              }
            }

            "Any non-numeric double" {
              nonNumericDoubles.forEach {
                POSITIVE_INFINITY shouldNotMatchLessThan it
              }
            }
          }

        }

        "Negative Infinity" - {

          "Should be less than" - {

            "Any numeric double" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                NEGATIVE_INFINITY shouldMatchLessThan it
              }
            }

            "Positive Infinity" {
              NEGATIVE_INFINITY shouldMatchLessThan POSITIVE_INFINITY
            }
          }

          "Should not be less than" - {

            "Itself" {
              NEGATIVE_INFINITY shouldNotMatchLessThan NEGATIVE_INFINITY
            }

            "NaN" {
              NEGATIVE_INFINITY shouldNotMatchLessThan NaN
            }
          }
        }
      }
    }

    "Positive matcher" - {

      "Zero" - {
        "Should not be positive" {
          0.0.shouldNotMatchPositive()
        }
      }
      "Every positive number" - {

        "Should be positive" {
          checkAll(100, numericDoubles.filterNot { it == 0.0 }) {
            it.absoluteValue.shouldMatchPositive()
          }
        }
      }

      "Every non-positive number" - {
        "Should not be positive" {
          checkAll(100, numericDoubles) {
            (-it.absoluteValue).shouldNotMatchPositive()
          }
        }
      }

      "The non-numeric double" - {
        "Positive Infinity" - {
          "Should be positive" {
            POSITIVE_INFINITY.shouldMatchPositive()
          }
        }

        "Negative Infinity" - {
          "Should not be positive" {
            NEGATIVE_INFINITY.shouldNotMatchPositive()
          }
        }

        "NaN" - {
          "Should not be positive" {
            NaN.shouldNotMatchPositive()
          }
        }
      }
    }

    "Negative matcher" - {

      "Zero" - {
        "Should not be negative" {
          0.0.shouldNotMatchNegative()
        }
      }
      "Every negative number" - {
        "Should be negative" {
          checkAll(100, numericDoubles.filterNot { it == 0.0 }) {
            (-it.absoluteValue).shouldMatchNegative()
          }
        }
      }

      "Every non-negative number" - {
        "Should not be negative" {
          checkAll(100, numericDoubles) {
            it.absoluteValue.shouldNotMatchNegative()
          }
        }
      }

      "The non-numeric double" - {
        "Positive Infinity" - {
          "Should not be negative" {
            POSITIVE_INFINITY.shouldNotMatchNegative()
          }
        }

        "Negative Infinity" - {
          "Should be negative" {
            NEGATIVE_INFINITY.shouldMatchNegative()
          }
        }

        "NaN" - {
          "Should not be negative" {
            NaN.shouldNotMatchNegative()
          }
        }
      }
    }

     "MultipleOf matcher" - {
        "Matches a simple multiple" {
           300.0 shouldBeMultipleOf 1.0
        }

        "Fails due to precision problems" {
           shouldFail {
              3.6e300 shouldBeMultipleOf 1.2
           }
        }
     }

    "Less than or equal matcher" - {
      "Every numeric double" - {
        "Should be less than or equal" - {

          "Itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchLessThanOrEqual it
            }
          }

          "Numbers bigger than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchLessThanOrEqual it.muchGreater()
              it shouldMatchLessThanOrEqual it.slightlyGreater()
            }
          }

          "Positive Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchLessThanOrEqual POSITIVE_INFINITY
            }
          }
        }

        "Should not be less than or equal" - {
          "Any number smaller than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThanOrEqual it.slightlySmaller()
              it shouldNotMatchLessThanOrEqual it.muchSmaller()
            }
          }

          "Negative Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThanOrEqual NEGATIVE_INFINITY
            }
          }

          "NaN" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThanOrEqual NaN
            }
          }
        }
      }

      "The non-numeric double" - {
        "NaN" - {
          "Should not be less than or equal" - {
            "Any numeric double" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                NaN shouldNotMatchLessThanOrEqual it
              }
            }

            "Positive Infinity" {
              NaN shouldNotMatchLessThanOrEqual POSITIVE_INFINITY
            }

            "Negative Infinity" {
              NaN shouldNotMatchLessThanOrEqual NEGATIVE_INFINITY
            }

            "Itself" {
              NaN shouldNotMatchLessThanOrEqual NaN
            }
          }
        }

        "Positive Infinity" - {

          "Should be less than or equal" - {
            "Positive Infinity" {
              POSITIVE_INFINITY shouldMatchLessThanOrEqual POSITIVE_INFINITY
            }
          }
          "Should not be less than or equal" - {
            "Any numeric double" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                POSITIVE_INFINITY shouldNotMatchLessThanOrEqual it
              }
            }

            "Negative Infinity" {
              POSITIVE_INFINITY shouldNotMatchLessThanOrEqual NEGATIVE_INFINITY
            }

            "NaN" {
              POSITIVE_INFINITY shouldNotMatchLessThanOrEqual NaN
            }
          }
        }

        "Negative Infinity" - {
          "Should be less than or equal" - {
            "Any numeric double" {
              checkAll(100, nonMinNorMaxValueDoubles) {
                NEGATIVE_INFINITY shouldMatchLessThanOrEqual it
              }
            }

            "Positive Infinity" {
              NEGATIVE_INFINITY shouldMatchLessThanOrEqual POSITIVE_INFINITY
            }

            "Itself" {
              NEGATIVE_INFINITY shouldMatchLessThanOrEqual NEGATIVE_INFINITY
            }
          }

          "Should not be less than or equal" - {
            "NaN" {
              NEGATIVE_INFINITY shouldNotMatchLessThanOrEqual NaN
            }
          }
        }
      }
    }

    "Greater than matcher" - {
      "Every numeric double" - {
        "Should be greater than" - {

          "Numbers smaller than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThan it.slightlySmaller()
              it shouldMatchGreaterThan it.muchSmaller()
            }
          }

          "Negative infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThan NEGATIVE_INFINITY
            }
          }
        }

        "Should not be greater than" - {

          "Itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan it
            }
          }

          "Numbers greater than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan it.slightlyGreater()
              it shouldNotMatchGreaterThan it.muchGreater()
            }
          }

          "NaN" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan NaN
            }
          }

          "Positive Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan POSITIVE_INFINITY
            }
          }
        }
      }

      "The non-numeric double" - {
        "NaN" - {
          "Should not be greater than" - {

            "Itself" {
              NaN shouldNotMatchGreaterThan NaN
            }

            "Any numeric double" {
              checkAll(100, numericDoubles) {
                NaN shouldNotMatchGreaterThan it
              }
            }

            "Positive Infinity" {
              NaN shouldNotMatchGreaterThan POSITIVE_INFINITY
            }

            "Negative Infinity" {
              NaN shouldNotMatchGreaterThan NEGATIVE_INFINITY
            }
          }
        }
      }
    }

    "Greater than or equal matcher" - {
      "Every numeric double" - {
        "Should be greater than or equal to" - {

          "Itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThanOrEqual it
            }
          }

          "Numbers smaller than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThanOrEqual it.slightlySmaller()
              it shouldMatchGreaterThanOrEqual it.muchSmaller()
            }
          }

          "Negative Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
          }
        }

        "Should not be greater than or equal to" - {
          "Numbers bigger than itself" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThanOrEqual it.slightlyGreater()
              it shouldNotMatchGreaterThanOrEqual it.muchGreater()

            }
          }

          "Positive Infinity" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThanOrEqual POSITIVE_INFINITY
            }
          }

          "NaN" {
            checkAll(100, nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThanOrEqual NaN
            }
          }
        }

      }

      "The non-numeric double" - {
        "NaN" - {
          "Should not be greater than or equal to" - {
            "Itself" {
              NaN shouldNotMatchGreaterThanOrEqual NaN
            }

            "Any numeric double" {
              checkAll(100, numericDoubles) {
                NaN shouldNotMatchGreaterThanOrEqual it
              }
            }

            "Positive Infinity" {
              NaN shouldNotMatchGreaterThanOrEqual POSITIVE_INFINITY
            }

            "Negative Infinity" {
              NaN shouldNotMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
          }
        }

        "Positive Infinity" - {
          "Should be greater than or equal to" - {
            "Itself" {
              POSITIVE_INFINITY shouldMatchGreaterThanOrEqual POSITIVE_INFINITY
            }

            "Negative Infinity" {
              POSITIVE_INFINITY shouldMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }

            "Any numeric double" {
              checkAll(100, numericDoubles) {
                POSITIVE_INFINITY shouldMatchGreaterThanOrEqual it
              }
            }
          }

          "Should not be greater than or equal to" - {
            "NaN" {
              POSITIVE_INFINITY shouldNotMatchGreaterThanOrEqual NaN
            }
          }
        }

        "Negative Infinity" - {
          "Should be greater than or equal to" - {
            "Itself" {
              NEGATIVE_INFINITY shouldMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
          }

          "Should not be greater than or equal to" - {
            "Any numeric double" {
              checkAll(100, numericDoubles) {
                NEGATIVE_INFINITY shouldNotMatchGreaterThanOrEqual it
              }
            }

            "Positive Infinity" {
              NEGATIVE_INFINITY shouldNotMatchGreaterThanOrEqual POSITIVE_INFINITY
            }

            "NaN" {
              NEGATIVE_INFINITY shouldNotMatchGreaterThanOrEqual NaN
            }
          }
        }
      }
    }

    "NaN matcher" - {
      "Every numeric double" - {
        "Should not be NaN" {
          checkAll(100, numericDoubles) {
            it.shouldNotMatchNaN()
          }
        }
      }

      "The non-numeric double" - {
        "NaN" - {
          "Should match NaN" {
            NaN.shouldMatchNaN()
          }
        }

        "Positive Infinity" - {
          "Should not match NaN" {
            POSITIVE_INFINITY.shouldNotMatchNaN()
          }
        }

        "Negative Infinity" - {
          "Should not match NaN" {
            NEGATIVE_INFINITY.shouldNotMatchNaN()
          }
        }
      }
    }

    "Positive Infinity matcher" - {
      "Any numeric double" - {
        "Should not match positive infinity" {
          checkAll(100, numericDoubles) {
            it.shouldNotMatchPositiveInfinity()
          }
        }
      }

      "The non-numeric double" - {
        "Positive Infinity" - {
          "Should match positive infinity" {
            POSITIVE_INFINITY.shouldMatchPositiveInfinity()
          }
        }

        "Negative Infinity" - {
          "Should not match positive infinity" {
            NEGATIVE_INFINITY.shouldNotMatchPositiveInfinity()
          }
        }

        "NaN" - {
          "Should not match positive infinity" {
            NaN.shouldNotMatchPositiveInfinity()
          }
        }
      }
    }


    "Negative Infinity matcher" - {
      "Any numeric double" - {
        "Should not match negative infinity" {
          checkAll(100, numericDoubles) {
            it.shouldNotMatchNegativeInfinity()
          }
        }
      }

      "The non-numeric double" - {
        "Negative Infinity" - {
          "Should match negative infinity" {
            NEGATIVE_INFINITY.shouldMatchNegativeInfinity()
          }
        }

        "Positive Infinity" - {
          "Should not match negative infinity" {
            POSITIVE_INFINITY.shouldNotMatchNegativeInfinity()
          }
        }

        "NaN" - {
          "Should not match negative infinity" {
            NaN.shouldNotMatchNegativeInfinity()
          }
        }
      }
    }

    "shouldBeZero" {
      (0.0).shouldBeZero()
      (-0.1).shouldNotBeZero()
      (0.1).shouldNotBeZero()
      MIN_VALUE.shouldNotBeZero()
      MAX_VALUE.shouldNotBeZero()
      NaN.shouldNotBeZero()
      POSITIVE_INFINITY.shouldNotBeZero()
      NEGATIVE_INFINITY.shouldNotBeZero()
    }
  }

  private fun shouldThrowAssertionError(message: String, vararg expression: () -> Any?) {
    expression.forEach {
      val exception = shouldThrow<AssertionError>(it)
      exception.message shouldBe message
    }
  }

  private fun Double.shouldMatchBetween(a: Double, b: Double, tolerance: Double) {
    this.shouldBeBetween(a, b, tolerance)
    this shouldBe between(a, b, tolerance)

    this.shouldThrowExceptionOnNotBetween(a, b, tolerance)
  }

  private fun Double.shouldNotMatchBetween(a: Double, b: Double, tolerance: Double) {
    this.shouldNotBeBetween(a, b, tolerance)
    this shouldNotBe between(a, b, tolerance)

    this.shouldThrowExceptionOnBetween(a, b, tolerance)
  }

  private fun Double.shouldThrowExceptionOnBetween(a: Double, b: Double, tolerance: Double) {
    when {
      this < a -> this.shouldThrowMinimumExceptionOnBetween(a, b, tolerance)
      this > b -> this.shouldThrowMaximumExceptionOnBetween(a, b, tolerance)
      else     -> throw IllegalStateException()
    }
  }

  private fun Double.shouldThrowMinimumExceptionOnBetween(a: Double, b: Double, tolerance: Double) {
    val message = "$this is outside expected range [$a, $b] (using tolerance $tolerance)"
    shouldThrowExceptionOnBetween(a, b, tolerance, message)
  }

  private fun Double.shouldThrowMaximumExceptionOnBetween(a: Double, b: Double, tolerance: Double) {
    val message = "$this is outside expected range [$a, $b] (using tolerance $tolerance)"
    shouldThrowExceptionOnBetween(a, b, tolerance, message)
  }


  private fun Double.shouldThrowExceptionOnBetween(
    a: Double,
    b: Double,
    tolerance: Double,
    message: String = "$this is outside expected range [$a, $b] (using tolerance $tolerance)"
  ) {
    shouldThrowAssertionError(message,
                              { this.shouldBeBetween(a, b, tolerance) },
                              { this shouldBe between(a, b, tolerance) })
  }

  private fun Double.shouldThrowExceptionOnNotBetween(
    a: Double,
    b: Double,
    tolerance: Double,
    message: String = "$this is not outside expected range [$a, $b] (using tolerance $tolerance)"
  ) {

    shouldThrowAssertionError(message,
                              { this.shouldNotBeBetween(a, b, tolerance) },
                              { this shouldNotBe between(a, b, tolerance) })
  }

  private infix fun Double.shouldMatchLessThan(x: Double) {
    this shouldBe lt(x)
    this shouldBeLessThan x
    this should beLessThan(x)

    this shouldThrowExceptionOnNotLessThan x
  }

  private infix fun Double.shouldThrowExceptionOnNotLessThan(x: Double) {
    shouldThrowAssertionError("$this should not be < $x",
                              { this shouldNotBe lt(x) },
                              { this shouldNotBeLessThan x },
                              { this shouldNot beLessThan(x) })
  }

  private infix fun Double.shouldNotMatchLessThan(x: Double) {
    this shouldNotBe lt(x)
    this shouldNotBeLessThan x
    this shouldNot beLessThan(x)

    this shouldThrowExceptionOnLessThan x
  }

  private infix fun Double.shouldThrowExceptionOnLessThan(x: Double) {
    shouldThrowAssertionError("$this should be < $x",
                              { this shouldBe lt(x) },
                              { this shouldBeLessThan x },
                              { this should beLessThan(x) }
    )
  }

  private fun Double.shouldMatchPositive() {
    this.shouldBePositive()
    this shouldBe positive()

    this.shouldThrowExceptionOnNotPositive()
  }

  private fun Double.shouldThrowExceptionOnNotPositive() {
    shouldThrowAssertionError("$this should not be > 0.0",
                              { this shouldNotBe positive() },
                              { this.shouldNotBePositive() }
    )
  }

  private fun Double.shouldNotMatchPositive() {
    this.shouldNotBePositive()
    this shouldNotBe positive()

    this.shouldThrowExceptionOnPositive()
  }

  private fun Double.shouldThrowExceptionOnPositive() {
    shouldThrowAssertionError("$this should be > 0.0",
                              { this shouldBe positive() },
                              { this.shouldBePositive() }
    )
  }

  private fun Double.shouldMatchNegative() {
    this.shouldBeNegative()
    this shouldBe negative()

    this.shouldThrowExceptionOnNotNegative()
  }

  private fun Double.shouldThrowExceptionOnNotNegative() {
    shouldThrowAssertionError("$this should not be < 0.0",
                              { this shouldNotBe negative() },
                              { this.shouldNotBeNegative() }
    )
  }

  private fun Double.shouldNotMatchNegative() {
    this.shouldNotBeNegative()
    this shouldNotBe negative()

    this.shouldThrowExceptionOnNegative()
  }

  private fun Double.shouldThrowExceptionOnNegative() {
    shouldThrowAssertionError("$this should be < 0.0",
                              { this shouldBe negative() },
                              { this.shouldBeNegative() }
    )
  }

  private infix fun Double.shouldMatchLessThanOrEqual(x: Double) {
    this should beLessThanOrEqualTo(x)
    this shouldBe lte(x)
    this shouldBeLessThanOrEqual x

    this shouldThrowExceptionOnNotLessThanOrEqual x
  }

  private infix fun Double.shouldThrowExceptionOnNotLessThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should not be <= $x",
                              { this shouldNot beLessThanOrEqualTo(x) },
                              { this shouldNotBe lte(x) },
                              { this shouldNotBeLessThanOrEqual x }
    )
  }

  private infix fun Double.shouldNotMatchLessThanOrEqual(x: Double) {
    this shouldNot beLessThanOrEqualTo(x)
    this shouldNotBe lte(x)
    this shouldNotBeLessThanOrEqual x

    this shouldThrowExceptionOnLessThanOrEqual x
  }

  private infix fun Double.shouldThrowExceptionOnLessThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should be <= $x",
                              { this should beLessThanOrEqualTo(x) },
                              { this shouldBe lte(x) },
                              { this shouldBeLessThanOrEqual x }
    )
  }

  private infix fun Double.shouldMatchGreaterThan(x: Double) {
    this should beGreaterThan(x)
    this shouldBe gt(x)
    this shouldBeGreaterThan x

    this shouldThrowExceptionOnNotGreaterThan x
  }

  private infix fun Double.shouldThrowExceptionOnNotGreaterThan(x: Double) {
    shouldThrowAssertionError("$this should not be > $x",
                              { this shouldNot beGreaterThan(x) },
                              { this shouldNotBeGreaterThan (x) },
                              { this shouldNotBe gt(x) })
  }

  private infix fun Double.shouldNotMatchGreaterThan(x: Double) {
    this shouldNot beGreaterThan(x)
    this shouldNotBe gt(x)
    this shouldNotBeGreaterThan x

    this shouldThrowExceptionOnGreaterThan (x)
  }

  private infix fun Double.shouldThrowExceptionOnGreaterThan(x: Double) {
    shouldThrowAssertionError("$this should be > $x",
                              { this should beGreaterThan(x) },
                              { this shouldBe gt(x) },
                              { this shouldBeGreaterThan x })
  }

  private infix fun Double.shouldMatchGreaterThanOrEqual(x: Double) {
    this should beGreaterThanOrEqualTo(x)
    this shouldBe gte(x)
    this shouldBeGreaterThanOrEqual x

    this shouldThrowExceptionOnNotGreaterThanOrEqual (x)
  }

  private infix fun Double.shouldThrowExceptionOnNotGreaterThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should not be >= $x",
                              { this shouldNot beGreaterThanOrEqualTo(x) },
                              { this shouldNotBe gte(x) },
                              { this shouldNotBeGreaterThanOrEqual x })
  }

  private infix fun Double.shouldNotMatchGreaterThanOrEqual(x: Double) {
    this shouldNot beGreaterThanOrEqualTo(x)
    this shouldNotBe gte(x)
    this shouldNotBeGreaterThanOrEqual x

    this shouldThrowExceptionOnGreaterThanOrEqual (x)
  }

  private infix fun Double.shouldThrowExceptionOnGreaterThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should be >= $x",
                              { this should beGreaterThanOrEqualTo(x) },
                              { this shouldBe gte(x) },
                              { this shouldBeGreaterThanOrEqual x })
  }

  private fun Double.shouldMatchNaN() {
    this should beNaN()
    this.shouldBeNaN()

    this.shouldThrowExceptionOnNotBeNaN()
  }

  private fun Double.shouldThrowExceptionOnNotBeNaN() {
    shouldThrowAssertionError("$this should not be NaN",
                              { this.shouldNotBeNaN() },
                              { this shouldNot beNaN() })
  }

  private fun Double.shouldNotMatchNaN() {
    this shouldNot beNaN()
    this.shouldNotBeNaN()

    this.shouldThrowExceptionOnBeNaN()
  }

  private fun Double.shouldThrowExceptionOnBeNaN() {
    shouldThrowAssertionError("$this should be NaN",
                              { this.shouldBeNaN() },
                              { this should beNaN() })
  }

  private fun Double.shouldMatchPositiveInfinity() {
    this should bePositiveInfinity()
    this.shouldBePositiveInfinity()

    this.shouldThrowExceptionOnNotBePositiveInfinity()
  }

  private fun Double.shouldThrowExceptionOnNotBePositiveInfinity() {
    shouldThrowAssertionError("$this should not be POSITIVE_INFINITY",
                              { this shouldNot bePositiveInfinity() },
                              { this.shouldNotBePositiveInfinity() })
  }

  private fun Double.shouldNotMatchPositiveInfinity() {
    this shouldNot bePositiveInfinity()
    this.shouldNotBePositiveInfinity()

    this.shouldThrowExceptionOnBePositiveInfinity()
  }

  private fun Double.shouldThrowExceptionOnBePositiveInfinity() {
    shouldThrowAssertionError("$this should be POSITIVE_INFINITY",
                              { this should bePositiveInfinity() },
                              { this.shouldBePositiveInfinity() })
  }


  private fun Double.shouldMatchNegativeInfinity() {
    this should beNegativeInfinity()
    this.shouldBeNegativeInfinity()

    this.shouldThrowExceptionOnNotBeNegativeInfinity()
  }

  private fun Double.shouldThrowExceptionOnNotBeNegativeInfinity() {
    shouldThrowAssertionError("$this should not be NEGATIVE_INFINITY",
            { this shouldNot beNegativeInfinity() },
            { this.shouldNotBeNegativeInfinity() })
  }

  private fun Double.shouldNotMatchNegativeInfinity() {
    this shouldNot beNegativeInfinity()
    this.shouldNotBeNegativeInfinity()

    this.shouldThrowExceptionOnBeNegativeInfinity()
  }

  private fun Double.shouldThrowExceptionOnBeNegativeInfinity() {
    shouldThrowAssertionError("$this should be NEGATIVE_INFINITY",
            { this should beNegativeInfinity() },
            { this.shouldBeNegativeInfinity() })
  }
}
