package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

abstract class StringSpecDuplicateNameTest(iso: IsolationMode) : StringSpec() {
   init {
      isolationMode = iso
      "foo" {}
      "foo" { this.testCase.name.name shouldBe "(1) foo" }
      "foo" { this.testCase.name.name shouldBe "(2) foo" }
   }
}

class StringSpecSingleInstanceDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.SingleInstance)
class StringSpecInstancePerLeafDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class StringSpecInstancePerTestDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerTest)
