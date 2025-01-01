package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// tests that multi line test names are normalized
class StringSpecMultilineNameTest : StringSpec() {
   init {

      val names = mutableSetOf<String>()

      afterSpec {
         names shouldBe setOf("multi line name test")
      }

      """multi line
         name test""" {
         names.add(this.testCase.name.name)
      }
   }
}
