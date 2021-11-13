package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.ExpectSpec

class ExpectBangTest : ExpectSpec() {

   init {
      context("!BangedContext") {
         error("OWWW!")
      }

      context("NonBangedContext") {
         expect("!BangedExpected") {
            error("POWIE!")
         }
      }

   }

}
