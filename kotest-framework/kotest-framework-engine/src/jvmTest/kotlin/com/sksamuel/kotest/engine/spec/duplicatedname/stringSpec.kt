package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

abstract class StringSpecDuplicateNameTest(iso: IsolationMode) : StringSpec() {
   init {
      isolationMode = iso
      "foo" {}
      shouldThrow<DuplicatedTestNameException> {
         "foo" {}
      }.message shouldBe "Cannot create test with duplicated name foo"
   }
}

class StringSpecSingleInstanceDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.SingleInstance)
class StringSpecInstancePerLeafDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class StringSpecInstancePerTestDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerTest)
