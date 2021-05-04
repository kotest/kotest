package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.matchers.shouldBe

abstract class ExpectSpecDuplicateTest(iso: IsolationMode) : ExpectSpec() {

   private val previous = configuration.duplicateTestNameMode

   init {

      beforeSpec {
         configuration.duplicateTestNameMode = DuplicateTestNameMode.Silent
      }

      isolationMode = iso
      context("foo") {
         expect("woo") {}
         expect("woo") { this.testCase.displayName shouldBe "(1) woo" }
         expect("woo") { this.testCase.displayName shouldBe "(2) woo" }
      }
      context("foo") {
         this.testCase.displayName shouldBe "(1) foo"
      }
      context("foo") {
         this.testCase.displayName shouldBe "(2) foo"
      }

      afterSpec {
         configuration.duplicateTestNameMode = previous
      }
   }
}

@Isolate
class ExpectSpecSingleInstanceDuplicateNameTest : ExpectSpecDuplicateTest(IsolationMode.SingleInstance)

@Isolate
class ExpectSpecInstancePerLeafDuplicateNameTest : ExpectSpecDuplicateTest(IsolationMode.InstancePerLeaf)

@Isolate
class ExpectSpecInstancePerTestDuplicateNameTest : ExpectSpecDuplicateTest(IsolationMode.InstancePerTest)
