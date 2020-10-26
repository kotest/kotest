package io.kotest.plugin.pitest.specs

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecs : WordSpec() {

  init {
    "should container" should {
      "passing test" { 1 shouldBe 1 }
      "failing test" { 1 shouldBe 2 }
    }
    "when container" `when` {
      "nested should container" should {
        "passing test" { 1 shouldBe 1 }
        "failing test" { 1 shouldBe 2 }
      }
    }
  }
}
