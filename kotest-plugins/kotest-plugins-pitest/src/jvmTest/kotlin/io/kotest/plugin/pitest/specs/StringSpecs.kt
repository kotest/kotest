package io.kotest.plugin.pitest.specs

import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class StringSpecs : StringSpec() {
  init {
    "passing test" { 1 shouldBe 1 }
    "failing test" { 1 shouldBe 2 }
  }
}