package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.matchers.shouldBe

abstract class FunSpecDuplicateNameTest(iso: IsolationMode) : FunSpec() {

   private val previous = configuration.duplicateTestNameMode


   init {

      beforeSpec {
         configuration.duplicateTestNameMode = DuplicateTestNameMode.Silent
      }

      isolationMode = iso
      context("wobble") {
         test("wibble") { }
         test("wibble") { this.testCase.displayName shouldBe "(1) wibble" }
         test("wibble") { this.testCase.displayName shouldBe "(2) wibble" }
      }
      context("wobble") { this.testCase.displayName shouldBe "(1) wobble" }
      context("wobble") { this.testCase.displayName shouldBe "(2) wobble" }

      afterSpec {
         configuration.duplicateTestNameMode = previous
      }
   }
}

@Isolate // sets global values via configuration so must be isolated
class FunSpecSingleInstanceDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.SingleInstance)

@Isolate // sets global values via configuration so must be isolated
class FunSpecInstancePerLeafDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)

@Isolate // sets global values via configuration so must be isolated
class FunSpecInstancePerTestDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerTest)

