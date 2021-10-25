package com.sksamuel.kotest.engine.extensions.spec.specextensions

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec

class SpecExtensionTest : FunSpec() {

   init {

      register(object : SpecExtension {
         override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
            execute(spec)
         }
      })

      test("A spec extension should be able to change the result") {
         error("boom!")
      }
   }
}
