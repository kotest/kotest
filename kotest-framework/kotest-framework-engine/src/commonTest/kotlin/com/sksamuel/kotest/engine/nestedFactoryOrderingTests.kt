package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestCaseOrder
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

class NestedFactoriesShouldInlineTest : FunSpec() {
   init {

      include(factory2)

      afterSpec {
         output shouldBe "worf"
         output = ""
      }
   }
}

class NestedFactoriesShouldRespectTestOrderFunctionOverrideTest : FunSpec() {

   override fun testCaseOrder() = TestCaseOrder.Lexicographic

   init {

      include(factory2)

      afterSpec {
         output shouldBe "forw"
         output = ""
      }
   }
}


class NestedFactoriesShouldRespectTestOrderVarTest : FunSpec() {
   init {

      testOrder = TestCaseOrder.Lexicographic

      include(factory2)

      afterSpec {
         output shouldBe "forw"
         output = ""
      }
   }
}
