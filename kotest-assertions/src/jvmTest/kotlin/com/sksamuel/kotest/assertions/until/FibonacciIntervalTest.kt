package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.until.fibonacci
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FibonacciIntervalTest : FunSpec() {

  init {
    test("fib correctness") {
      fibonacci(0) shouldBe 0
      fibonacci(1) shouldBe 1
      fibonacci(2) shouldBe 1
      fibonacci(3) shouldBe 2
      fibonacci(4) shouldBe 3
      fibonacci(5) shouldBe 5
      fibonacci(6) shouldBe 8
      fibonacci(7) shouldBe 13
    }
  }

}
