package com.sksamuel.kotlintest.until

import io.kotlintest.until.fibonacci
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FibonacciDelayTest : FunSpec() {

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