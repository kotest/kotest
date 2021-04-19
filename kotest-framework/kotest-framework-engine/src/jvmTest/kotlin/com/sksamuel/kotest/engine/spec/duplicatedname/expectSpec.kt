package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

abstract class ExpectSpecDuplicateNameTest(iso: IsolationMode) : ExpectSpec() {
   init {
      isolationMode = iso
      context("foo") {
         expect("woo") {}
         expect("woo") { this.testCase.displayName shouldBe "woo (1)" }
      }
      context("foo") {
         this.testCase.displayName shouldBe "foo (1)"
      }
   }
}

class ExpectSpecSingleInstanceDuplicateNameTest : ExpectSpecDuplicateNameTest(IsolationMode.SingleInstance)
class ExpectSpecInstancePerLeafDuplicateNameTest : ExpectSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class ExpectSpecInstancePerTestDuplicateNameTest : ExpectSpecDuplicateNameTest(IsolationMode.InstancePerTest)
