package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

private var beforeInstancePerTest = 0
private var afterInstancePerTest = 0

class IsolationTestExtension : SpecExtension {
   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      beforeInstancePerTest++
      execute(spec)
      afterInstancePerTest++
   }
}

@ApplyExtension(IsolationTestExtension::class)
class SpecExtensionIsolationModePerTestTest : WordSpec() {
   init {

      isolationMode = IsolationMode.InstancePerTest

      afterProject {
         beforeInstancePerTest shouldBe 6
         afterInstancePerTest shouldBe 6
      }

      "SpecExtensions" should {
         "fire first for this instance" {
         }
         "fire again for this instance" {
         }
         "fire again for this instance 2" {
         }
         "fire again for this instance 3" {
         }
      }
   }
}
