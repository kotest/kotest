package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.style.FunSpec

class EnabledTestConfigFlagTest : FunSpec() {
   init {

      test("ignored by enabled flag").config(enabled = false) {
         error("boom")
      }

      test("ignored by enabledIf flag").config(enabledIf = { false }) {
         error("boom")
      }
   }
}
