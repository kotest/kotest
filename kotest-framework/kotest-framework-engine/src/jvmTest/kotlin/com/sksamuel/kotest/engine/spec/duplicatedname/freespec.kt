package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec

abstract class FreeSpecDuplicateNameTest(iso: IsolationMode) : FreeSpec() {

   init {
      isolationMode = iso

      "wibble" { }
      shouldThrow<DuplicatedTestNameException> {
         "wibble" { }
      }

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
