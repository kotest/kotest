package com.sksamuel.kotest.specs.wordspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.specs.WordSpec

class WordSpecBangTest : WordSpec() {

  init {
    "!BangedOuter" should {
      attemptToFail()
    }
  }

}