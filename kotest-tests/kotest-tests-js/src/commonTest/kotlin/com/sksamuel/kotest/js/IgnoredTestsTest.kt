package com.sksamuel.kotest.js

import io.kotest.core.spec.style.FunSpec

class IgnoredTestsTest : FunSpec() {
   init {
      test("not ignored") {

      }
      test("!ignored by a bang") {
         error("boom")
      }
      test("ignored by config").config(enabled = false) {
         error("boom")
      }
      xtest("ignored by xmethods") {
         error("boom")
      }
   }
}
