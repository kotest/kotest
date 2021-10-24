package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.ShouldSpec

class ShouldSpecBangTest : ShouldSpec() {

   init {
      should("!BangedShould") {
         error("THWAPP!")
      }

      context("!BangedContext") {
         error("SLOSH!")
      }

      context("NonBangedOuter") {
         context("!BangedInner") {
            error("PAM!")
         }
         context("NonBangedInner") {
            should("!BangedShould") {
               error("SOCK!")
            }
         }
      }
   }
}
