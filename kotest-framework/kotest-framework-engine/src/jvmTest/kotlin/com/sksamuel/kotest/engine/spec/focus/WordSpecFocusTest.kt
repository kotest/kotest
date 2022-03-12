package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecFocusTest : WordSpec() {

   private var results = ""

   override suspend fun afterSpec(spec: Spec) {
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
