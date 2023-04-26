package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.shouldBe

fun factory1(writer: StringBuilder) = funSpec {
   test("o") {
      writer.append(this.testCase.name.testName)
   }
   test("r") {
      writer.append(this.testCase.name.testName)
   }
}

fun factory2(writer: StringBuilder) = funSpec {
   test("w") {
      writer.append(this.testCase.name.testName)
   }
   include(factory1(writer))
   test("f") {
      writer.append(this.testCase.name.testName)
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

class FactoryInContainerTest : FunSpec({
   context("foo") {
      include(factory1(StringBuilder()))

      test("bar") {
         1 shouldBe 2
      }
   }
})

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

      testOrder = TestCaseOrder.Lexicographic

      val writer = StringBuilder()
      include(factory2(writer))

      afterSpec {
         writer.toString() shouldBe "forw"
      }
   }
}
