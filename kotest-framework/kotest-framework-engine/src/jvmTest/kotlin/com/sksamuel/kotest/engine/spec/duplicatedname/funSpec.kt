package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec

abstract class FunSpecDuplicateNameTest(iso: IsolationMode) : FunSpec() {
   init {
      isolationMode = iso
      context("wobble") {
         test("wibble") { }
         shouldThrow<DuplicatedTestNameException> {
            test("wibble") {}
         }
      }
      shouldThrow<DuplicatedTestNameException> {
         context("wobble") {}
      }
   }
}

class FunSpecSingleInstanceDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FunSpecInstancePerLeafDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FunSpecInstancePerTestDuplicateNameTest : FunSpecDuplicateNameTest(IsolationMode.InstancePerTest)

