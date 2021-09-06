package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.toDescription
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

object SpecExtensionNumbers {

   var before = 0
   var after = 0

   val ext = object : SpecInterceptExtension {
      override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
         if (spec.toDescription().name.qualifiedName == SpecExtensionTest::class.java.name) {
            before++
            process()
            after++
         } else {
            process()
         }
      }
   }
}

class SpecExtensionTest : WordSpec() {

   init {

      register(SpecExtensionNumbers.ext)

      afterProject {
         SpecExtensionNumbers.after shouldBe 1
      }

      "SpecExtensions" should {
         "be activated by registration with ProjectExtensions" {
            SpecExtensionNumbers.before shouldBe 1
            SpecExtensionNumbers.after shouldBe 0
         }
         "only be fired once per spec class" {
            // the intercepts should not have fired again
            SpecExtensionNumbers.before shouldBe 1
            SpecExtensionNumbers.after shouldBe 0
         }
      }
   }
}
