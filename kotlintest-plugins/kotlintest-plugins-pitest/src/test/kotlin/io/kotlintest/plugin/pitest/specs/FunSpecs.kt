package io.kotlintest.plugin.pitest.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FunSpecs : FunSpec() {
  init {
    test("passing test") { 1 shouldBe 1 }
    test("failing test") { 1 shouldBe 2 }
  }
}