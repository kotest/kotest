package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.matchers.shouldBe

abstract class StringSpecDuplicateNameTest(iso: IsolationMode) : StringSpec() {

   private val previous = configuration.duplicateTestNameMode

   init {

      beforeSpec {
         configuration.duplicateTestNameMode = DuplicateTestNameMode.Silent
      }

      isolationMode = iso

      "foo" {}
      "foo" { this.testCase.displayName shouldBe "(1) foo" }
      "foo" { this.testCase.displayName shouldBe "(2) foo" }

      afterSpec {
         configuration.duplicateTestNameMode = previous
      }
   }
}

@Isolate // sets global values via configuration so must be isolated
class StringSpecSingleInstanceDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.SingleInstance)

@Isolate // sets global values via configuration so must be isolated
class StringSpecInstancePerLeafDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)

@Isolate // sets global values via configuration so must be isolated
class StringSpecInstancePerTestDuplicateNameTest : StringSpecDuplicateNameTest(IsolationMode.InstancePerTest)
