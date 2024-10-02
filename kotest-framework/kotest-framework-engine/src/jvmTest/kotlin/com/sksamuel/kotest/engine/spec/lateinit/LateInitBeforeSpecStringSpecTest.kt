package com.sksamuel.kotest.engine.spec.lateinit

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec

@EnabledIf(LinuxCondition::class)
class LateInitBeforeSpecStringSpecTest : StringSpec() {

   private lateinit var string: String

   override suspend fun beforeSpec(spec: Spec) {
      string = "Hello"
   }

   init {
      "Hello should equal to Hello" {
         string shouldBe "Hello"
      }
   }
}
