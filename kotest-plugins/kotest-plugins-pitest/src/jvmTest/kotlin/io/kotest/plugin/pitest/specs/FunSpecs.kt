package io.kotest.plugin.pitest.specs

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecs : FunSpec() {
  init {
    test("passing test") { 1 shouldBe 1 }
    test("failing test") { 1 shouldBe 2 }
  }
}
