package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.FunSpec

class FunSpecBangTest : FunSpec() {

  init {
     test("!BangedTest") {
        error("ZLOTT!")
     }
     context("!banged context") {
        error("ZAMMM!")
     }
     context("non banged context") {
        test("!banged inner") {
           error("SWOOSH!")
        }
     }
  }

}
