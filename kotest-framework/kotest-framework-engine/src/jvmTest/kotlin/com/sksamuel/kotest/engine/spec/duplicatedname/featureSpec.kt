package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

abstract class FeatureSpecDuplicateNameTest(iso: IsolationMode) : FeatureSpec() {
   init {
      isolationMode = iso
      feature("foo") {
         scenario("woo") {}
         scenario("woo") {
            this.testCase.displayName shouldBe "woo (1)"
         }
      }
      feature("foo") {
         this.testCase.displayName shouldBe "foo (1)"
      }
   }
}

class FeatureSpecSingleInstanceDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FeatureSpecInstancePerLeafDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FeatureSpecInstancePerTestDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerTest)
