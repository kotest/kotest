package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec

/**
 * Tests that by default, private classes are not executed.
 */
private class PrivateClassesAreIgnoredTest : FunSpec() {
   init {
      test("should not be invoked") {
         error("boom")
      }
   }
}
