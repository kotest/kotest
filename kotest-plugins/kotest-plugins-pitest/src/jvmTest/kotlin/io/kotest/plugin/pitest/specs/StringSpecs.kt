package io.kotest.plugin.pitest.specs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringSpecs : StringSpec() {
  init {
    "passing test" { 1 shouldBe 1 }
    "failing test" { 1 shouldBe 2 }
  }
}
