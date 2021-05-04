package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.matchers.shouldBe

abstract class FreeSpecDuplicateNameTest(iso: IsolationMode) : FreeSpec() {

   private val previous = configuration.duplicateTestNameMode

   init {

      beforeSpec {
         configuration.duplicateTestNameMode = DuplicateTestNameMode.Silent
      }

      isolationMode = iso

      "foo" { }
      "foo" { this.testCase.displayName shouldBe "(1) foo" }
      "foo" { this.testCase.displayName shouldBe "(2) foo" }

      "woo" - {
         "goo" { }
         "goo" {
            this.testCase.displayName shouldBe "(1) goo"
         }
         "goo" {
            this.testCase.displayName shouldBe "(2) goo"
         }
      }

      "woo" - {
         this.testCase.displayName shouldBe "(1) woo"
      }

      "woo" - {
         this.testCase.displayName shouldBe "(2) woo"
      }

      afterSpec {
         configuration.duplicateTestNameMode = previous
      }
   }
}

@Isolate // sets global values via configuration so must be isolated
class FreeSpecSingleInstanceDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.SingleInstance)

@Isolate // sets global values via configuration so must be isolated
class FreeSpecInstancePerLeafDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)

@Isolate // sets global values via configuration so must be isolated
class FreeSpecInstancePerTestDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerTest)
