package io.kotlintest.plugin.pitest.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class WordSpecs : WordSpec() {

  init {
    "should container" should {
      "passing test" { 1 shouldBe 1 }
      "failing test" { 1 shouldBe 2 }
    }
    "when container" `when` {
      "should container" should {
        "passing test" { 1 shouldBe 1 }
        "failing test" { 1 shouldBe 2 }
      }
    }
  }
}