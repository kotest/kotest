package io.kotlintest.samples.springboot

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class MyTest : FunSpec({
  test("A test") {
    1 + 1 shouldBe 2
  }
})
