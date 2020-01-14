package com.sksamuel.kotest.listeners

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.style.WordSpec
import io.kotest.shouldBe

class TestListenerTest : WordSpec() {

   var a: String? = null

   override fun beforeSpec(spec: SpecConfiguration) {
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
