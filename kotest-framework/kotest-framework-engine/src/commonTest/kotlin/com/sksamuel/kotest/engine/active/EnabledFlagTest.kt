package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.style.FunSpec

class IgnoredTestsTest : FunSpec() {
   init {

      test("ignored by config").config(enabled = false) {
         error("boom")
      }

      test("ignored by enabledIf").config(enabledIf = { false }) {
         error("boom")
      }
   }
}
