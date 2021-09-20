package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

abstract class FeatureSpecDuplicateNameTest(iso: IsolationMode) : FeatureSpec() {
   init {
      isolationMode = iso
      feature("foo") {
         scenario("woo") {}
         scenario("woo") { this.testCase.name.testName shouldBe "(1) woo" }
         scenario("woo") { this.testCase.name.testName shouldBe "(2) woo" }
      }
      feature("foo") { this.testCase.name.testName shouldBe "(1) foo" }
      feature("foo") { this.testCase.name.testName shouldBe "(2) foo" }
   }
}

class FeatureSpecSingleInstanceDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FeatureSpecInstancePerLeafDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FeatureSpecInstancePerTestDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerTest)
