package com.sksamuel.kotlintest.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlinx.coroutines.experimental.delay

class WordSpecSuspendTest : WordSpec() {
  init {
    "some context" should {
      "support suspend" {
        delay(10)
        1 shouldBe 1
      }
    }
  }
}