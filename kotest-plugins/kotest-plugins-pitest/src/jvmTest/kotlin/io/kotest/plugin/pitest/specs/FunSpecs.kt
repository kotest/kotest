package io.kotest.plugin.pitest.specs

import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class FunSpecs : FunSpec() {
  init {
    test("passing test") { 1 shouldBe 1 }
    test("failing test") { 1 shouldBe 2 }
  }
}