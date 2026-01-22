package com.sksamuel.kotest.engine.spec.xmethod

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class FeatureSpecRootXFocusedTests : FeatureSpec() {
   init {

      var tests = 0

      afterSpec {
         tests shouldBe 2
      }

      feature("root test without config") {
         error("boom") // will be ignored because of a focused test
      }

      feature("root test with config").config(timeout = 10.seconds) {
         error("boom") // will be ignored because of a focused test
      }

      ffeature("focused root test without config") {
         tests++
      }

      ffeature("focused root test with config").config(timeout = 10.seconds) {
         tests++
      }

      xfeature("disabled root test without config") {
         error("boom") // will be ignored because of xmethod
      }

      xfeature("disabled root test with config").config(timeout = 10.seconds) {
         error("boom") // will be ignored because of xmethod
      }
   }
}
