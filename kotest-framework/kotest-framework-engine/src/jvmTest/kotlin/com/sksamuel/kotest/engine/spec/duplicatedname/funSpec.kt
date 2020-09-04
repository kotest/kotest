package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

abstract class FunSpecDuplicateNameTest(iso: IsolationMode) : FunSpec() {
   init {
      isolationMode = iso
      context("wobble") {
         test("wibble") { }
         shouldThrow<DuplicatedTestNameException> {
            test("wibble") {}
         }.message shouldBe "Cannot create test with duplicated name wibble"
      }
      shouldThrow<DuplicatedTestNameException> {
         context("wobble") {}
      }
   }
}

class FunSpecSingleInstanceDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FunSpecInstancePerLeafDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FunSpecInstancePerTestDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerTest)

