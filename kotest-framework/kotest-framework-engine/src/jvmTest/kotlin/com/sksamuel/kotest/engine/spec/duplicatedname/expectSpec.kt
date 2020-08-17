package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ExpectSpec

abstract class ExpectSpecDuplicateNameTest(iso: IsolationMode) : ExpectSpec() {
   init {
      isolationMode = iso
      context("foo") {
         expect("woo") {}
         shouldThrow<DuplicatedTestNameException> {
            expect("woo") {}
         }
      }
      shouldThrow<DuplicatedTestNameException> {
         context("foo") {}
      }
   }
}

class ExpectSpecSingleInstanceDuplicateNameTest : ExpectSpecDuplicateNameTest(IsolationMode.SingleInstance)
class ExpectSpecInstancePerLeafDuplicateNameTest : ExpectSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class ExpectSpecInstancePerTestDuplicateNameTest : ExpectSpecDuplicateNameTest(IsolationMode.InstancePerTest)
