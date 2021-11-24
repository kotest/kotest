package com.sksamuel.kotest.engine.names

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RootTestsUniqueNamesTest : FunSpec() {
   init {
      test("foo") { this.testCase.name.testName shouldBe "foo" }
      test("bar") { this.testCase.name.testName shouldBe "bar" }
      test("bar") { this.testCase.name.testName shouldBe "(1) bar" }
   }
}
