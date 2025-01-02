package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

abstract class FeatureSpecDuplicateNameTest(iso: IsolationMode) : FeatureSpec() {
   init {
      isolationMode = iso
      feature("foo") {
         scenario("woo") {}
         scenario("woo") { this.testCase.name.name shouldBe "(1) woo" }
         scenario("woo") { this.testCase.name.name shouldBe "(2) woo" }
      }
      feature("foo") {
         this.testCase.name.name shouldBe "(1) foo"
         scenario("woo") {}
      }
      feature("foo") {
         this.testCase.name.name shouldBe "(2) foo"
         scenario("woo") {}
      }
   }
}

class FeatureSpecSingleInstanceDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FeatureSpecInstancePerRootDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerRoot)
