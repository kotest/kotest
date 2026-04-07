package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.style.ExpectSpec

class ExpectSpecFocusTest : ExpectSpec() {
   init {
      fcontext("focused context") {}
      context("not focused container") {
         error("boom")
      }
      fexpect("focused expect") {}
      expect("not focused test") {
         error("boom")
      }
   }
}
