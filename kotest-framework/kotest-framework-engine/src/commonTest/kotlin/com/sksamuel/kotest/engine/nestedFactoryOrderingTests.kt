package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.shouldBe

fun factory1(writer: StringBuilder) = funSpec {
   test("o") {
      writer.append(this.testCase.name.name)
   }
   test("r") {
      writer.append(this.testCase.name.name)
   }
}

fun factory2(writer: StringBuilder) = funSpec {
   test("w") {
      writer.append(this.testCase.name.name)
   }
   include(factory1(writer))
   test("f") {
      writer.append(this.testCase.name.name)
   }
}

class NestedFactoriesShouldInlineTest : FunSpec() {
   init {

      val writer = StringBuilder()
      include(factory2(writer))

      afterSpec {
         writer.toString() shouldBe "worf"
      }
   }
}

class NestedFactoriesShouldRespectTestOrderFunctionOverrideTest : FunSpec() {

   override fun testCaseOrder() = TestCaseOrder.Lexicographic

   init {

      val writer = StringBuilder()
      include(factory2(writer))

      afterSpec {
         writer.toString() shouldBe "forw"
      }
   }
}


class NestedFactoriesShouldRespectTestOrderVarTest : FunSpec() {
   init {

      testCaseOrder = TestCaseOrder.Lexicographic

      val writer = StringBuilder()
      include(factory2(writer))

      afterSpec {
         writer.toString() shouldBe "forw"
      }
   }
}
