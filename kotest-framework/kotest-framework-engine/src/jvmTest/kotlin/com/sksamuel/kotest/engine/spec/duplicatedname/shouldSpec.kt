package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec

abstract class ShouldSpecDuplicateNameTest(iso: IsolationMode) : ShouldSpec() {
   init {
      isolationMode = iso
      context("foo") {
         should("woo") {}
         shouldThrow<DuplicatedTestNameException> {
            should("woo") {}
         }
      }
      shouldThrow<DuplicatedTestNameException> {
         context("foo") {}
      }
   }
}

class ShouldSpecSingleInstanceDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.SingleInstance)
class ShouldSpecInstancePerLeafDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class ShouldSpecInstancePerTestDuplicateNameTest : ShouldSpecDuplicateNameTest(IsolationMode.InstancePerTest)
