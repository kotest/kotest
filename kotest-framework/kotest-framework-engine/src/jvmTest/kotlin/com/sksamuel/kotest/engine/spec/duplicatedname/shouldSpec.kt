package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

abstract class ShouldSpecDuplicateNameTest(iso: IsolationMode) : ShouldSpec() {
   init {
      isolationMode = iso
      context("foo") {
         should("woo") {}
         should("woo") { this.testCase.name.name shouldBe "(1) woo" }
         should("woo") { this.testCase.name.name shouldBe "(2) woo" }
      }
      context("foo") {
         this.testCase.name.name shouldBe "(1) foo"
         should("a") { }
      }
      context("foo") {
         this.testCase.name.name shouldBe "(2) foo"
         should("a") { }
      }
   }
}

class ShouldSpecSingleInstanceDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.SingleInstance)
class ShouldSpecInstancePerLeafDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class ShouldSpecInstancePerTestDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerTest)
