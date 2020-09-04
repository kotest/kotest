package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

abstract class FreeSpecDuplicateNameTest(iso: IsolationMode) : FreeSpec() {

   init {
      isolationMode = iso

      "wibble" { }
      shouldThrow<DuplicatedTestNameException> {
         "wibble" { }
      }.message shouldBe "Cannot create test with duplicated name wibble"

      "wobble" - {
         "wibble" { }
         shouldThrow<DuplicatedTestNameException> {
            "wibble" { }
         }
      }
   }
}

class FreeSpecSingleInstanceDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FreeSpecInstancePerLeafDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FreeSpecInstancePerTestDuplicateNameTest : FreeSpecDuplicateNameTest(IsolationMode.InstancePerTest)
