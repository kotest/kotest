package com.sksamuel.kotest.matchers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.and
import io.kotest.matchers.comparables.between
import io.kotest.matchers.or
import io.kotest.matchers.shouldBe

class LogicalMatchOperatorsTest : StringSpec({

  "and should pass if both tests pass" {
    1 shouldBe (between(0, 2) and between(1, 3))
  }

  "and should fail if first test fails" {
    shouldThrow<AssertionError> {
      1 shouldBe (between(2, 3) and between(1, 3))
    }
  }

  "and should fail if second test fails" {
    shouldThrow<AssertionError> {
      1 shouldBe (between(0, 3) and between(4, 5))
    }
  }

  "or should pass if both tests pass" {
    1 shouldBe (between(0, 2) or between(0, 3))
  }

  "or should pass if first test fails and second passes" {
    1 shouldBe (between(2, 3) or between(1, 3))
  }

  "or should pass if first test passes and second fails" {
    1 shouldBe (between(2, 3) or between(1, 3))
  }

  "or should fail if both tests fail" {
    shouldThrow<AssertionError> {
      1 shouldBe (between(2, 3) or between(4, 5))
    }
  }

  "should support multiple nested clauses" {
    1 shouldBe ((between(1, 2) and between(1,
      4)) or between(4, 5))
    1 shouldBe ((between(5, 6) and between(7,
      8)) or between(1, 5))
  }
})
