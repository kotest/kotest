package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringSpecMultilineNameTest : StringSpec() {
   init {

      val names = mutableSetOf<String>()

      afterSpec {
         names shouldBe setOf("multi line         name test")
      }

      """multi line
         name test""" {
         names.add(this.testCase.displayName)
      }
   }
}
