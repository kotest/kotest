package com.sksamuel.kotest.engine.extensions.spec.specextensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

private var beforeInstancePerRoot = 0
private var afterInstancePerRoot = 0

class IsolationLeafExtension : SpecExtension {
   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      beforeInstancePerRoot++
      execute(spec)
      afterInstancePerRoot++
   }
}

@ApplyExtension(IsolationLeafExtension::class)
@EnabledIf(LinuxCondition::class)
class WordSpecExtensionInstancePerRootTest : WordSpec() {
   init {

      isolationMode = IsolationMode.InstancePerRoot

      afterProject {
         beforeInstancePerRoot shouldBe 3
         afterInstancePerRoot shouldBe 3
      }
      "SpecExtensions" should {
         "fire first for this instance" {
         }
      }
      "And then" should {
         "fire again for this instance" {
         }
      }
      "Also" should {
         "fire for this one" {
         }
      }
   }
}
