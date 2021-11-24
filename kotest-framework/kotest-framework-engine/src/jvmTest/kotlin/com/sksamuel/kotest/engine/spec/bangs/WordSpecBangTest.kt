package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.WordSpec

class WordSpecBangTest : WordSpec() {

  init {
     "!BangedOuter" should {
        error("KAPOW!")
     }
     "Outer" should {
        "!banged inner" {
           error("AWKKKKKK!")
        }
     }
  }
}
