package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec

abstract class StringSpecDuplicateNameTest(iso: IsolationMode) : StringSpec() {
   init {
      isolationMode = iso
      "foo" {}
      shouldThrow<DuplicatedTestNameException> {
         "foo" {}
      }
   }
}

class StringSpecSingleInstanceDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.SingleInstance)
class StringSpecInstancePerLeafDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class StringSpecInstancePerTestDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerTest)
