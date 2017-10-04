package io.kotlintest.matchers

import io.kotlintest.specs.ShouldSpec

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
  }
}