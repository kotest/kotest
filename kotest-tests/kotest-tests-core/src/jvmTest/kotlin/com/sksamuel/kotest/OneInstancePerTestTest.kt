package com.sksamuel.kotest

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class OneInstancePerTestTest : FunSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  init {
    var count = 0
    test("be 0") {
      count shouldBe 0
      count = 100
    }
    test("be 0 part 2") {
      count shouldBe 0
      count = 100
    }
    test("be 0 part 3") {
      count shouldBe 0
      count = 100
    }
    test("still be 0") {
      count shouldBe 0
    }
  }
}
