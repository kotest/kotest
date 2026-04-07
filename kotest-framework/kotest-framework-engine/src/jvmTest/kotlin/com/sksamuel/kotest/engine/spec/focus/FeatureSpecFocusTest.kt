package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.style.FeatureSpec

class FeatureSpecFocusTest : FeatureSpec() {
   init {
      ffeature("focused context") {}
      feature("not focused container") {
         error("boom")
      }
   }
}
