package com.sksamuel.kotlintest.specs.wordspec

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.WordSpec

class WordSpecBangTest : WordSpec() {

  init {
    "!BangedOuter" should {
      attemptToFail()
    }
  }

}