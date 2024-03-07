package com.sksamuel.kotest.engine.extensions.spec.specextensions

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

private var beforeInstancePerLeaf = 0
private var afterInstancePerLeaf = 0

class IsolationLeafExtension : SpecExtension {
   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      beforeInstancePerLeaf++
      execute(spec)
      afterInstancePerLeaf++
   }
}

@ApplyExtension(IsolationLeafExtension::class)
class SpecExtensionIsolationModePerLeafTest : WordSpec() {
   init {

      isolationMode = IsolationMode.InstancePerLeaf

      afterProject {
         beforeInstancePerLeaf shouldBe 3
         afterInstancePerLeaf shouldBe 3
      }

      "SpecExtensions" should {
         "fire first for this instance" {
         }
         "fire again for this instance" {
         }
         "and this one" {
         }
      }
   }
}
