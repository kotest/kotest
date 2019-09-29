package com.sksamuel.kotest.until

import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import io.kotest.until.fibonacci

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