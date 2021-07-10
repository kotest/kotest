package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TestCaseOrderTest : FunSpec() {
   init {
      test("sequential test case ordering") {
         SequentialSpec().materializeAndOrderRootTests().map { it.testCase.description.name.name } shouldBe
            listOf("c", "b", "d", "e", "a")
      }
      test("Lexicographic test case ordering") {
         LexicographicSpec().materializeAndOrderRootTests().map { it.testCase.description.name.name } shouldBe
            listOf("a", "b", "c", "d", "e")
      }
      test("random test case ordering") {
         val a = RandomSpec().materializeAndOrderRootTests().map { it.testCase.description.name.name }
         val b = RandomSpec().materializeAndOrderRootTests().map { it.testCase.description.name.name }
         a shouldNotBe b
      }
   }
}

class SequentialSpec : StringSpec() {

   override fun testCaseOrder() = TestCaseOrder.Sequential

   init {
      "c" {}
      "b" {}
      "d" {}
      "e" {}
      "a" {}
   }
}

class LexicographicSpec : StringSpec() {

   override fun testCaseOrder() = TestCaseOrder.Lexicographic

   init {
      "b" {}
      "d" {}
      "a" {}
      "e" {}
      "c" {}
   }
}


class RandomSpec : StringSpec() {
   override fun testCaseOrder() = TestCaseOrder.Random

   init {
      "a" {}
      "b" {}
      "c" {}
      "d" {}
      "e" {}
      "f" {}
      "g" {}
      "h" {}
      "i" {}
      "j" {}
      "k" {}
      "l" {}
      "m" {}
      "n" {}
      "o" {}
      "p" {}
   }
}
