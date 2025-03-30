package com.sksamuel.kotest.engine.extensions.spec.specextensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class LateinitSpecExtensionWordSpecTest : WordSpec() {

   private lateinit var string: String

   inner class Interceptor : SpecExtension {
      override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
         this@LateinitSpecExtensionWordSpecTest.string = "Hello"
         execute(spec)
      }
   }

   override val extensions = listOf(Interceptor())

   init {
      "setting a late init var" should {
         "be supported by word spec" {
            string shouldBe "Hello"
         }
      }
   }
}
