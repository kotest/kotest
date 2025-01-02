package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

abstract class FreeSpecDuplicateNameTest(iso: IsolationMode) : FreeSpec() {
   init {
      isolationMode = iso

      "foo" { }
      "foo" { this.testCase.name.name shouldBe "(1) foo" }
      "foo" { this.testCase.name.name shouldBe "(2) foo" }

      "woo" - {
         "goo" { }
         "goo" {
            this.testCase.name.name shouldBe "(1) goo"
         }
         "goo" {
            this.testCase.name.name shouldBe "(2) goo"
         }
      }

      "woo" - {
         this.testCase.name.name shouldBe "(1) woo"
         "a" { }
      }

      "woo" - {
         this.testCase.name.name shouldBe "(2) woo"
         "a" { }
      }
   }
}

class FreeSpecSingleInstanceDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FreeSpecInstancePerLeafDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FreeSpecInstancePerTestDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerTest)
