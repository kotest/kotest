package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.matchers.shouldBe

abstract class DescribeSpecDuplicateTestNameModeInfoTest(iso: IsolationMode) : DescribeSpec() {

   private val previous = configuration.duplicateTestNameMode

   init {

      beforeSpec {
         configuration.duplicateTestNameMode = DuplicateTestNameMode.Silent
      }

      isolationMode = iso
      describe("foo") {
         it("woo") {}
         it("woo") {
            this.testCase.displayName shouldBe "(1) woo"
         }
         it("woo") {
            this.testCase.displayName shouldBe "(2) woo"
         }
      }
      describe("foo") {
         this.testCase.displayName shouldBe "(1) foo"
      }
      describe("foo") {
         this.testCase.displayName shouldBe "(2) foo"
      }
      context("goo") {}
      context("goo") {
         this.testCase.displayName shouldBe "(1) goo"
      }
      context("goo") {
         this.testCase.displayName shouldBe "(2) goo"
      }

      afterSpec {
         configuration.duplicateTestNameMode = previous
      }
   }
}

@Isolate // sets global values via configuration so must be isolated
class DescribeSpecSingleInstanceDuplicateTestNameModeInfoTest : DescribeSpecDuplicateTestNameModeInfoTest(IsolationMode.SingleInstance)

@Isolate // sets global values via configuration so must be isolated
class DescribeSpecInstancePerLeafDuplicateTestNameModeInfoTest : DescribeSpecDuplicateTestNameModeInfoTest(IsolationMode.InstancePerLeaf)

@Isolate // sets global values via configuration so must be isolated
class DescribeSpecInstancePerTestDuplicateTestNameModeInfoTest : DescribeSpecDuplicateTestNameModeInfoTest(IsolationMode.InstancePerTest)
