package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringSpecMultilineNameTest : StringSpec() {
   init {

      var count = 0

      afterSpec {
         count shouldBe 1
      }

      """multi line
         name test""" {
         count++
      }
   }
}
