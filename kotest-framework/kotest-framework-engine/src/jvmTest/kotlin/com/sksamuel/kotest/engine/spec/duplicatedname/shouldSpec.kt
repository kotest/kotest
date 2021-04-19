package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

abstract class ShouldSpecDuplicateNameTest(iso: IsolationMode) : ShouldSpec() {
   init {
      isolationMode = iso
      context("foo") {
         should("woo") {}
         should("woo") {
            this.testCase.displayName shouldBe "woo (1)"
         }
      }
      context("foo") {
         this.testCase.displayName shouldBe "foo (1)"
      }
   }
}

class ShouldSpecSingleInstanceDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.SingleInstance)
class ShouldSpecInstancePerLeafDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class ShouldSpecInstancePerTestDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerTest)
