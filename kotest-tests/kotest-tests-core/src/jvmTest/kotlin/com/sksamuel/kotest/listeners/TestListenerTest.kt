package com.sksamuel.kotest.listeners

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestListenerTest : WordSpec() {

   var a: String? = null

   override fun beforeSpec(spec: Spec) {
      a = "wibble"
   }

   init {
      "TestListener" should {
         "invoke before each spec" {
            a shouldBe "wibble"
         }
      }
   }
}
