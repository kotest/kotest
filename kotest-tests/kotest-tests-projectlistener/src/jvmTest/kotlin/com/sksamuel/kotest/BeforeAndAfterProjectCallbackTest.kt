package com.sksamuel.kotest

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class BeforeAndAfterProjectCallbackTest : WordSpec() {
   init {
       "project config" should {
          "call before project" {
             MyConfigGlobalState.beforeProjectCallCount shouldBe 1
          }

          "call before all" {
             MyConfigGlobalState.beforeAllCallCount shouldBe 1
          }
       }
   }
}
