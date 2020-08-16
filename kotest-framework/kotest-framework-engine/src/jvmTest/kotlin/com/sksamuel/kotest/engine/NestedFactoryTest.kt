package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe

private var output = ""

val factory1 = funSpec {
   test("o") {
      output += this.testCase.displayName
   }
   test("r") {
      output += this.testCase.displayName
   }
}

val factory2 = funSpec {
   test("w") {
      output += this.testCase.displayName
   }
   include(factory1)
   test("f") {
      output += this.testCase.displayName
   }
}

class NestedFactoryTest : FunSpec() {
   init {

      include(factory2)

      test("nested factories should be inlined in order") {
         output shouldBe "worf"
      }
   }
}
