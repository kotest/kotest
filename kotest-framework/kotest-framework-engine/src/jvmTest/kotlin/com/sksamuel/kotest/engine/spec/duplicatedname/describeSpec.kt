package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

abstract class DescribeSpecDuplicateTestNameModeInfoTest(iso: IsolationMode) : DescribeSpec() {
   init {
      isolationMode = iso
      describe("foo") {
         it("woo") {}
         it("woo") {
            this.testCase.name.name shouldBe "(1) woo"
         }
         it("woo") {
            this.testCase.name.name shouldBe "(2) woo"
         }
      }
      describe("foo") {
         this.testCase.name.name shouldBe "(1) foo"
         it("a") { }
      }
      describe("foo") {
         this.testCase.name.name shouldBe "(2) foo"
         it("a") { }
      }
      context("goo") {
         it("a") { }
      }
      context("goo") {
         this.testCase.name.name shouldBe "(1) goo"
         it("a") { }
      }
      context("goo") {
         this.testCase.name.name shouldBe "(2) goo"
         it("a") { }
      }
   }
}

class DescribeSpecSingleInstanceDuplicateTestNameModeInfoTest : DescribeSpecDuplicateTestNameModeInfoTest(IsolationMode.SingleInstance)
class DescribeSpecInstancePerLeafDuplicateTestNameModeInfoTest : DescribeSpecDuplicateTestNameModeInfoTest(IsolationMode.InstancePerLeaf)
class DescribeSpecInstancePerTestDuplicateTestNameModeInfoTest : DescribeSpecDuplicateTestNameModeInfoTest(IsolationMode.InstancePerTest)
