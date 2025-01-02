package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

abstract class FunSpecDuplicateNameTest(iso: IsolationMode) : FunSpec() {
   init {
      isolationMode = iso
      context("wobble") {
         test("wibble") { }
         test("wibble") { this.testCase.name.name shouldBe "(1) wibble" }
         test("wibble") { this.testCase.name.name shouldBe "(2) wibble" }
      }
      context("wobble") {
         this.testCase.name.name shouldBe "(1) wobble"
         test("a") { }
      }
      context("wobble") {
         this.testCase.name.name shouldBe "(2) wobble"
         test("a") { }
      }
   }
}

class FunSpecSingleInstanceDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FunSpecInstancePerLeafDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FunSpecInstancePerTestDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerTest)

