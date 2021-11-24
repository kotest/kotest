package com.sksamuel.kotest.engine.active

import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec

@Isolate
@Ignored
annotation class ComposedAnno

@ComposedAnno
class IgnoredComposedAnnotationTest : FunSpec() {
   init {
      error("Boom")
      test("should not be invoked") {
         error("Boom")
      }
   }
}
