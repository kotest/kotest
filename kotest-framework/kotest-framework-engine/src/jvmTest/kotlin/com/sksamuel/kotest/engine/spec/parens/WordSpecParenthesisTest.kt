package com.sksamuel.kotest.engine.spec.parens

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecParenthesisTest : WordSpec() {

   var names = mutableListOf<String>()

   override suspend fun afterSpec(spec: Spec) {
      names.shouldBe(listOf("parenthesis (here)", "and (here)"))
   }

   init {
      "parenthesis (here) " should {
         names.add(this.testCase.name.name)
         "and (here)" {
            names.add(this.testCase.name.name)
         }
      }
   }
}
