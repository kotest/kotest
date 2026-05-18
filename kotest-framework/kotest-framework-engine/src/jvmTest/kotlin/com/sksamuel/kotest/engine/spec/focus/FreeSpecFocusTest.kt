package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.style.FreeSpec

class FreeSpecFocusTest : FreeSpec() {
   init {
      "f: focused context: " - {
      }
      "normal context: " - {
         error("boom")
      }
      "f: focused top level test" {}
      "normal top level test" {
         error("boom")
      }
   }
}
