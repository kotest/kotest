package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

abstract class ShouldSpecDuplicateNameTest(iso: IsolationMode) : ShouldSpec() {
   init {
      isolationMode = iso
      context("foo") {
         should("woo") {}
         should("woo") { this.testCase.displayName shouldBe "(1) woo" }
         should("woo") { this.testCase.displayName shouldBe "(2) woo" }
      }
      context("foo") { this.testCase.displayName shouldBe "(1) foo" }
      context("foo") { this.testCase.displayName shouldBe "(2) foo" }
   }
}

@Isolate // sets global values via configuration so must be isolated
class ShouldSpecSingleInstanceDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.SingleInstance)

@Isolate // sets global values via configuration so must be isolated
class ShouldSpecInstancePerLeafDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)

@Isolate // sets global values via configuration so must be isolated
class ShouldSpecInstancePerTestDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerTest)
