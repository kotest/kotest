package com.sksamuel.kotest.engine

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.shouldBe

class TestCaseOrderTest : FunSpec() {
   init {
      test("sequential test case ordering specified in the spec") {
         Materializer(ProjectConfiguration()).materialize(SequentialSpec()).map { it.name.testName } shouldBe
            listOf("c", "b", "d", "e", "a")
      }
      test("Lexicographic test case ordering specified in the spec") {
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
