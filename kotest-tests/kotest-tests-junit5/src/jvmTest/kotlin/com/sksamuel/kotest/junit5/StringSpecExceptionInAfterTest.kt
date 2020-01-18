package com.sksamuel.kotest.junit5

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec

internal class StringSpecExceptionInAfterTest : StringSpec() {

  init {
    "a failing test" {
      1 shouldBe 2
    }

    "a passing test" {
      1 shouldBe 1
    }
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    throw RuntimeException("craack!!")
  }
}

internal class StringSpecExceptionInAfterTestFunction : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      afterTest {
         throw RuntimeException("craack!!")
      }
   }
}
