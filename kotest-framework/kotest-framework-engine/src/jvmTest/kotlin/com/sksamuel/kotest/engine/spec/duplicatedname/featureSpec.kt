package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.matchers.shouldBe

abstract class FeatureSpecDuplicateNameTest(iso: IsolationMode) : FeatureSpec() {

   private val previous = configuration.duplicateTestNameMode

   init {

      beforeSpec {
         configuration.duplicateTestNameMode = DuplicateTestNameMode.Silent
      }

      isolationMode = iso
      feature("foo") {
         scenario("woo") {}
         scenario("woo") { this.testCase.displayName shouldBe "(1) woo" }
         scenario("woo") { this.testCase.displayName shouldBe "(2) woo" }
      }
      feature("foo") { this.testCase.displayName shouldBe "(1) foo" }
      feature("foo") { this.testCase.displayName shouldBe "(2) foo" }

      afterSpec {
         configuration.duplicateTestNameMode = previous
      }
   }
}

@Isolate // sets global values via configuration so must be isolated
class FeatureSpecSingleInstanceDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.SingleInstance)

@Isolate // sets global values via configuration so must be isolated
class FeatureSpecInstancePerLeafDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)

@Isolate // sets global values via configuration so must be isolated
class FeatureSpecInstancePerTestDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerTest)
