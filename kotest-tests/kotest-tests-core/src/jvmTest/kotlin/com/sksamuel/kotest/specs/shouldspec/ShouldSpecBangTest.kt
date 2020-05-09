package com.sksamuel.kotest.specs.shouldspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.ShouldSpec

class ShouldSpecBangTest : ShouldSpec() {

   init {
      should("!BangedShould") {
         attemptToFail()
      }

      context("!BangedContext") {
         attemptToFail()
      }

      context("NonBangedOuter") {
         context("!BangedInner") {
            attemptToFail()
         }
         context("NonBangedInner") {
            should("!BangedShould") {
               attemptToFail()
            }
         }
      }
   }
}
