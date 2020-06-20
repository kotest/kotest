package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecFocusTest : WordSpec() {

   var results = ""

   override fun afterSpec(spec: Spec) {
      results shouldBe "ab"
   }

   init {
      "f:focused outer" should {
         results += "a"
         "execute inner" {
            results += "b"
         }
      }

      "non focused outer" should {
         results += "c"
         "be ignored" {
            results += "d"
         }
      }
   }
}
