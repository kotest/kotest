package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringSpecFocusTest : StringSpec() {

   private var order = ""

   override suspend fun afterSpec(spec: Spec) {
      order shouldBe "ac"
   }

   init {
      "f:a" {
         order += "a"
      }
      "b" {
         error("boom")
      }
      """
         f:
         c""" {
         order += "c"
      }
   }
}
