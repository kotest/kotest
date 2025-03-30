package com.sksamuel.kotest.engine.extensions.spec.specextensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class LateinitSpecExtensionStringSpecTest : StringSpec() {

   private lateinit var string: String

   inner class Interceptor : SpecExtension {
      override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
         this@LateinitSpecExtensionStringSpecTest.string = "Hello"
         execute(spec)
      }
   }

   override val extensions = listOf(Interceptor())

   init {
      "Hello should equal to Hello" {
         string shouldBe "Hello"
      }
   }
}
