package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

abstract class StringSpecDuplicateNameTest(iso: IsolationMode) : StringSpec() {
   init {
      isolationMode = iso
      "foo" {}
      "foo" {
         this.testCase.displayName shouldBe "foo (1)"
      }
   }
}

class StringSpecSingleInstanceDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.SingleInstance)
class StringSpecInstancePerLeafDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class StringSpecInstancePerTestDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerTest)
