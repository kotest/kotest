package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

abstract class DescribeSpecDuplicateNameTest(iso: IsolationMode) : DescribeSpec() {
   init {
      isolationMode = iso
      describe("foo") {
         it("woo") {}
         it("woo") {
            this.testCase.displayName shouldBe "woo (1)"
         }
      }
      describe("foo") {
         this.testCase.displayName shouldBe "foo (1)"
      }
      context("goo") {}
      context("goo") {
         this.testCase.displayName shouldBe "goo (1)"
      }
   }
}

class DescribeSpecSingleInstanceDuplicateNameTest : DescribeSpecDuplicateNameTest(IsolationMode.SingleInstance)
class DescribeSpecInstancePerLeafDuplicateNameTest : DescribeSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class DescribeSpecInstancePerTestDuplicateNameTest : DescribeSpecDuplicateNameTest(IsolationMode.InstancePerTest)
