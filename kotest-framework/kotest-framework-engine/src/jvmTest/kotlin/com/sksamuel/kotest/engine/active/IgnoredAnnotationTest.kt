package com.sksamuel.kotest.engine.active

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec

@Ignored
class IgnoredAnnotationTest : FunSpec() {
  init {
    error("Boom")
    test("should not be invoked") {
      error("Boom")
    }
  }
}
