package com.sksamuel.kotest.engine.extensions.spec.specextensions

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SpecExtensionSingleInstanceTest : WordSpec() {

   init {

      var before = 0
      var after = 0

      extension(object : SpecExtension {
         override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
            before++
            execute(spec)
            after++
         }
      })

      afterProject {
         before shouldBe 1
         after shouldBe 1
      }

      "SpecExtensions" should {
         "fire only once for this single instance" {
         }
         "and not for repeated tests" {
         }
      }
   }
}
