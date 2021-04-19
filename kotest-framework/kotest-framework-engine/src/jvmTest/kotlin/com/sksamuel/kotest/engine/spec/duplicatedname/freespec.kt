package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

abstract class FreeSpecDuplicateNameTest(iso: IsolationMode) : FreeSpec() {

   init {
      isolationMode = iso

      "foo" { }
      "foo" {
         this.testCase.displayName shouldBe "foo (1)"
      }

      "woo" - {
         "goo" { }
         "goo" {
            this.testCase.displayName shouldBe "goo (1)"
         }
      }

      "woo" - {
         this.testCase.displayName shouldBe "woo (1)"
      }
   }
}

class FreeSpecSingleInstanceDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FreeSpecInstancePerLeafDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FreeSpecInstancePerTestDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerTest)
